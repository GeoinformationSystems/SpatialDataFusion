package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 */
public class BPMNParser extends AbstractOperation {

    private static final String PROCESS_TITLE = BPMNParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for OMG BPMN format";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "BPMN resource";

    private final static String OUT_BPMN_TITLE = "OUT_MODEL";
    private final static String OUT_BPMN_DESCRIPTION = "Parsed BPMN Model";

    /**
     * constructor
     */
    public BPMNParser() {
        super(null, PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //get input data
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        URL resourceURL = ((URLLiteral) resourceConnector.getData()).resolve();
        //parse bpmn
        CamundaBPMNModel model;
        try {
            model = parseBPMN(resourceURL);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Could not parse BPMN source", e);
        }
        //set output connector
        connectOutput(OUT_BPMN_TITLE, model);
    }

    /**
     * parse BPMN from URL
     *
     * @param resourceURL input URL
     * @return BPMN model
     * @throws IOException
     */
    private CamundaBPMNModel parseBPMN(URL resourceURL) throws IOException, ParserConfigurationException, SAXException {
        //parse HTTP connection
        if (resourceURL.getProtocol().toLowerCase().startsWith("http"))
            return parseBPMNFromHTTP(resourceURL);
            //parse file
        else if (resourceURL.getProtocol().toLowerCase().startsWith("file"))
            return parseBPMNFromFile(resourceURL);
            //else: throw IOException
        else
            throw new IOException("Unsupported BPMN source: " + resourceURL.toString());
    }

    /**
     * parse BPMN from HTTP
     *
     * @param resourceURL input URL
     * @return BPMN model
     * @throws IOException
     */
    private CamundaBPMNModel parseBPMNFromHTTP(URL resourceURL) throws IOException, ParserConfigurationException, SAXException {
        //get connection
        HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
        urlConnection.connect();
        return parseBPMN(resourceURL, urlConnection.getInputStream());
    }

    /**
     * parse JSON from file
     *
     * @param resourceURL input URL (set as identifier)
     * @return GeoTools feature collection
     * @throws IOException
     */
    private CamundaBPMNModel parseBPMNFromFile(URL resourceURL) throws IOException, ParserConfigurationException, SAXException {
        File file = new File(resourceURL.getFile());
        if (!file.exists() || file.isDirectory())
            return null;
        //redirect based on content type
        return parseBPMN(resourceURL, new FileInputStream(file));
    }

    /**
     * parse BPMN model
     * @param resourceURL input url
     * @param inputStream input stream
     * @return BPMN model
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private CamundaBPMNModel parseBPMN(URL resourceURL, InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        IIdentifier identifier = new Identifier(resourceURL.toString());
        CamundaBPMNModel model = new CamundaBPMNModel(identifier, "BPMN Model", null);
        initModel(model, document);
        return model;
    }

    private void initModel(CamundaBPMNModel model, Document document) {

    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_BPMN_TITLE, OUT_BPMN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(CamundaBPMNModel.class),
                        new MandatoryDataConstraint()},
                null);
    }
}
