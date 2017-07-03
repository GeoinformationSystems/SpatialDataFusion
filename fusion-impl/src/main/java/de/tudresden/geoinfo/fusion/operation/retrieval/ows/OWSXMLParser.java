package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.Nullable;
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

public abstract class OWSXMLParser extends AbstractOperation {

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "Link to OWS XML document";

    /**
     * constructor
     *
     */
    public OWSXMLParser(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    public Document getDocument() throws SAXException, IOException, ParserConfigurationException {
        //get URL
        URLLiteral uriLiteral = getResourceURI();
        //parse HTTP connection
        if (uriLiteral.resolve().getProtocol().toLowerCase().startsWith("http"))
            return parseDocumentFromHTTP(uriLiteral);
            //parse file
        else if (uriLiteral.resolve().getProtocol().toLowerCase().startsWith("file"))
            return parseDocumentFromFile(uriLiteral);
        else
            throw new IllegalArgumentException("Unsupported OWS resource");
    }

    protected URLLiteral getResourceURI() {
        return ((URLLiteral) getInputConnector(IN_RESOURCE_TITLE).getData());
    }

    private Document parseDocumentFromHTTP(URLLiteral uriLiteral) throws IOException, SAXException, ParserConfigurationException {
        HttpURLConnection connection = (HttpURLConnection) uriLiteral.resolve().openConnection();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("OWS resource is not valid or not accessible, HTTP response " + connection.getResponseCode());
        return parse(connection.getInputStream());
    }

    private Document parseDocumentFromFile(URLLiteral uriLiteral) throws SAXException, IOException, ParserConfigurationException {
        File file = new File(uriLiteral.resolve().getFile());
        if (!file.exists() || file.isDirectory())
            throw new IllegalArgumentException("Cannot read OWS resource");
        return parse(new FileInputStream(file));
    }

    private Document parse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        //parse document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        //close
        inputStream.close();
        //return document
        return doc;
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

}
