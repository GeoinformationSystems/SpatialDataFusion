package de.tudresden.geoinfo.fusion.operation.ows;

import de.tud.fusion.XMLBuilder;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.*;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess.WPSProcessDescription;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess.WPSProcessDescription.WPSIODescription;
import de.tudresden.geoinfo.fusion.data.ows.XMLResponse;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ElementState;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.WPSDescriptionParser;
import de.tudresden.geoinfo.fusion.operation.workflow.InputConnector;
import de.tudresden.geoinfo.fusion.operation.workflow.OutputConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Proxy WPS operation instance
 */
public class WPSProxy extends OWSServiceOperation {

    private static final String PROCESS_TITLE = WPSProxy.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Proxy for OGC WPS";

    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String OUT_DESCRIPTION = "OUT_DESCRIPTION";

    private static final String PROCESS_OUTPUT = ".*(?i)Output$";
    private static final String IO_IDENTIFIER = ".*(?i)Identifier$";
    private static final String IO_REFERENCE = ".*(?i)Reference$";
    private static final String IO_REFERENCE_ATT = ".*(?i)href$";
    private static final String IO_REFERENCE_SCHEMA = ".*(?i)schema$";
    private static final String IO_REFERENCE_TYPE = ".*(?i)mimeType$";

    private final static String PARAM_SERVICE = "service";
    private final static String PARAM_REQUEST = "request";
    private final static String PARAM_IDENTIFIER = "identifier";
    private final static String VALUE_SERVICE = "WPS";
    private final static String VALUE_DESCRIBEPROCESS = "describeProcess";
    private final static String PARAM_VERSION = "version";
    private final static String VALUE_DEFAULT_VERSION = "1.0.0";

    private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Collections.singletonList("1.0.0"));

    private final static String REGEX_IGNORED_OUTPUT = "(?i)(OUT_RUNTIME)|(OUT_START)";

    private WPSProcessDescription processDescription;
    private String processId;

    /**
     * constructor
     * @param identifier instance identifier
     * @param base WPS base url
     */
    public WPSProxy(@Nullable IIdentifier identifier, @NotNull URLLiteral base) {
        super(identifier, PROCESS_TITLE, PROCESS_DESCRIPTION, base, false);
    }

    /**
     * initialize WPS proxy for specified process
     * @param processId process identifier
     */
    public void setProcessId(@NotNull String processId) throws IOException {
        this.processId = processId;
        this.setTitle(processId);
        initProcessDescription(processId);
        super.initializeConnectors();
    }

    /**
     * get selected process identifier
     * @return process identifier
     */
    public String getProcessId() {
        return this.processId;
    }

    @Override
    public @Nullable String getSelectedOffering(){
        return this.getProcessId();
    }

    /**
     * initialize process description
     *
     * @return process description
     * @throws IOException
     */
    private void initProcessDescription(@NotNull String processId) throws IOException {

        WPSDescriptionParser parser = new WPSDescriptionParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_DESCRIPTION = parser.getOutputConnector(OUT_DESCRIPTION).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, getDescribeProcessRequest());

        Map<IIdentifier, IData> output = parser.execute(input);
        if (!output.containsKey(ID_OUT_DESCRIPTION) || !(output.get(ID_OUT_DESCRIPTION) instanceof WPSDescribeProcess))
            throw new IOException("Could not parse WPS process description for " + processId);
        WPSDescribeProcess processDescriptions = (WPSDescribeProcess) output.get(ID_OUT_DESCRIPTION);
        if (!processDescriptions.getProcessIdentifier().contains(processId))
            throw new IOException("WPS does not provide process with identifier " + processId);
        this.processDescription = processDescriptions.getProcessDescription(processId);

    }

    /**
     * get process description request
     *
     * @return process description request
     */
    private URLLiteral getDescribeProcessRequest() throws MalformedURLException {
        this.setRequest(VALUE_DESCRIBEPROCESS);
        this.setParameter(PARAM_VERSION, VALUE_DEFAULT_VERSION);
        this.setParameter(PARAM_IDENTIFIER, this.getProcessId());
        return this.getRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST, PARAM_VERSION, PARAM_IDENTIFIER}, new String[]{});
    }

    /**
     * get underlying WPS process description
     *
     * @return WPS process description
     */
    public WPSProcessDescription getProcessDescription() {
        return this.processDescription;
    }

    @Override
    public String getService() {
        return VALUE_SERVICE;
    }

    @Override
    public @NotNull String getDefaultVersion() {
        return VALUE_DEFAULT_VERSION;
    }

    @Override
    public @NotNull Set<String> getSupportedVersions() {
        return SUPPORTED_VERSIONS;
    }

    @Override
    public @NotNull WPSCapabilities getCapabilities() {
        return (WPSCapabilities) super.getCapabilities();
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWPSProcesses();
    }

    @Override
    public void execute() {
        String request = getXMLRequest();
        request = request.replace("&", "&amp;");
        try {
            XMLResponse response = executeRequest(request);
            setOutput(response);
        } catch (IOException | SAXException | ParserConfigurationException | URISyntaxException e) {
            this.setState(ElementState.ERROR);
            e.printStackTrace();
        }
    }

    private XMLResponse executeRequest(String request) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        //init connection
        System.out.println(request);
        URL url = this.getProcessDescription().getURI().getBase();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Length", String.valueOf(request.getBytes().length));
        //send request
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(request);
        out.flush();
        out.close();
        //get response
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(connection.getInputStream());
        return new XMLResponse(new URLLiteral(url), document, null);
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        for (WPSIODescription input : this.processDescription.getInputs().values()) {
            IInputConnector connector = new InputConnector(new Identifier(input.getIdentifier()), input.getTitle(), input.getDescription(), this, input.getRuntimeConstraints(), input.getConnectionConstraints(), null);
            this.addInputConnector(connector);
        }
    }

    @Override
    public void initializeOutputConnectors() {
        for (WPSIODescription output : this.processDescription.getOutputs().values()) {
            IOutputConnector connector = new OutputConnector(new Identifier(output.getIdentifier()), output.getTitle(), output.getDescription(), this, output.getRuntimeConstraints(), output.getConnectionConstraints());
            this.addOutputConnector(connector);
        }
    }

    @NotNull
    private String getXMLRequest() {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        XMLBuilder builder = new XMLBuilder("wps", "Execute", null, null, null);
        //add xml namespace attributes
        builder.addAttribute("service", "wps");
        builder.addAttribute("version", "1.0.0");
        builder.addAttribute("encoding", "UTF-8");
        builder.addAttribute("xmlns:wps", "http://www.opengis.net/wps/1.0.0");
        builder.addAttribute("xmlns:ows", "http://www.opengis.net/ows/1.1");
        builder.addAttribute("xmlns:ogc", "http://www.opengis.net/ogc");
        builder.addAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        builder.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        builder.addAttribute("xsi:schemaLocation", "http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsExecute_request.xsd");
        //add wps identifier
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, this.getProcessId(), null));
        //add inputs
        builder.addChildNode(getDataInputs());
        //add response
        builder.addChildNode(getResponseForm());
        //return
        return header + builder.toString();
    }

    private XMLBuilder getDataInputs(){
        XMLBuilder builder = new XMLBuilder("wps", "DataInputs", null, null, null);
        for (IInputConnector connector : this.getInputConnectors()) {
            if(connector.getData() != null)
                builder.addChildNode(getInput(connector));
        }
        return builder;
    }

    private XMLBuilder getInput(@NotNull IInputConnector inputConnector){
        XMLBuilder builder = new XMLBuilder("wps", "Input", null, null, null);
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, inputConnector.getTitle(), null));
        builder.addChildNode(getData(inputConnector.getData()));
        return builder;
    }

    private XMLBuilder getData(@Nullable IData data){
        if(data instanceof URLLiteral)
            return getData((URLLiteral) data);
        if(data instanceof LiteralData)
            return getData((LiteralData) data);
        //TODO add support for complex data
        throw new RuntimeException("complex input is not supported");
    }

    private XMLBuilder getData(URLLiteral data){
        XMLBuilder builder = new XMLBuilder("wps", "Reference", null, null, null);
        if(data.getIOFormat() != null && data.getIOFormat().getMimetype() != null)
            builder.addAttribute("mimeType", data.getIOFormat().getMimetype());
        if(data.getIOFormat() != null && data.getIOFormat().getSchema() != null)
            builder.addAttribute("schema", data.getIOFormat().getSchema());
        builder.addAttribute("xlink:href", data.resolve().toString());
        builder.addAttribute("method", "GET");
        return builder;
    }

    private String getSchema(IOFormat defaultFormat) {
        return defaultFormat.getSchema();
    }

    private XMLBuilder getData(@NotNull LiteralData data){
        XMLBuilder builder = new XMLBuilder("wps", "Data", null, null, null);
        builder.addChildNode(getLiteralData(data));
        return builder;
    }

    private XMLBuilder getLiteralData(@NotNull LiteralData data){
        XMLBuilder builder = new XMLBuilder("wps", "LiteralData", null, data.getLiteral(), null);
        builder.addAttribute("dataType", getLiteralType(data));
        return builder;
    }

    private String getLiteralType(@NotNull LiteralData data) {
        if(data instanceof DecimalLiteral)
            return "xs:double";
        if(data instanceof LongLiteral)
            return "xs:long";
        if(data instanceof IntegerLiteral)
            return "xs:integer";
        if(data instanceof BooleanLiteral)
            return "xs:boolean";
        else
            return "xs:string";
    }

    private XMLBuilder getResponseForm() {
        XMLBuilder builder = new XMLBuilder("wps", "ResponseForm", null, null, null);
        builder.addChildNode(getResponseDocument());
        return builder;
    }

    private XMLBuilder getResponseDocument() {
        XMLBuilder builder = new XMLBuilder("wps", "ResponseDocument", null, null, null);
        builder.addAttribute("storeExecuteResponse", "false");
        builder.addAttribute("lineage", "false");
        builder.addAttribute("status", "false");
        for (IOutputConnector connector : this.getOutputConnectors()) {
            if(!connector.getTitle().matches(REGEX_IGNORED_OUTPUT))
                builder.addChildNode(getOutput(connector));
        }
        return builder;
    }

    private XMLBuilder getOutput(@NotNull IOutputConnector outputConnector) {
        XMLBuilder builder = new XMLBuilder("wps", "Output", null, null, null);
        builder.addAttribute("asReference", "true");
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, outputConnector.getTitle(), null));
        return builder;
    }

    private void setOutput(XMLResponse response) throws MalformedURLException {
        List<Node> outputs = response.getNodes(PROCESS_OUTPUT);
        for(Node output : outputs){
            setOutput(output);
        }
    }

    private void setOutput(Node output) throws MalformedURLException {
        String identifier = null;
        String reference = null;
        String mimeType = null;
        String schema = null;
        NodeList layerNodes = output.getChildNodes();
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Node element = layerNodes.item(i);
            if (element.getNodeName().matches(IO_IDENTIFIER)) {
                identifier = element.getTextContent().trim();
            }
            else if (element.getNodeName().matches(IO_REFERENCE)) {
                NamedNodeMap attributes = element.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    if (attribute.getNodeName().matches(IO_REFERENCE_ATT))
                        reference = attribute.getNodeValue().trim();
                    else if (attribute.getNodeName().matches(IO_REFERENCE_TYPE))
                        mimeType = attribute.getNodeValue().trim();
                    else if (attribute.getNodeName().matches(IO_REFERENCE_SCHEMA))
                        schema = attribute.getNodeValue().trim();
                }
            }
        }
        if(identifier == null || reference == null)
            throw new RuntimeException("Could not determine output reference");
        //set connector data
        this.getOutputConnector(identifier).setData(new URLLiteral(new URL(reference), new IOFormat(mimeType, schema, null)));
    }

}
