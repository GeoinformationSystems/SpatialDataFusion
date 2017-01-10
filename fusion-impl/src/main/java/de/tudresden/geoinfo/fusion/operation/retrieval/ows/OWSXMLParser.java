package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.InputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class OWSXMLParser extends AbstractOperation {

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

	/**
	 * constructor
	 * @param identifier process identifier
	 */
	public OWSXMLParser(IIdentifier identifier) {
		super(identifier);
	}
	
	public Document getDocument() throws SAXException, IOException, ParserConfigurationException {
		//get URL
		URL resourceURL = getResourceURL();
		//parse HTTP connection
		if(resourceURL.getProtocol().toLowerCase().startsWith("http"))
			return parseDocumentFromHTTP(resourceURL);		
		//parse file
		else if(resourceURL.getProtocol().toLowerCase().startsWith("file"))
			return parseDocumentFromFile(resourceURL);
		else
			throw new IllegalArgumentException("Unsupported OWS resource");
	}
	
	public IIdentifier getDocumentIdentifier() {
		return new Identifier(getResourceURL().toString());
	}
	
	private URL getResourceURL(){
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		//get URL
		try {
			return ((URILiteral) resourceConnector.getData()).resolve().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("OWS document resource is not a valid URL", e);
		}
	}
	
	private Document parseDocumentFromHTTP(URL resourceURL) throws IOException, SAXException, ParserConfigurationException {
		HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("OWS resource is not valid or not accessible, HTTP response " + connection.getResponseCode());
		return parse(resourceURL.toString(), connection.getInputStream());
	}
	
	private Document parseDocumentFromFile(URL resourceURL) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(resourceURL.getFile());
		if(!file.exists() || file.isDirectory())
			throw new IllegalArgumentException("Cannot read OWS resource");
		return parse(resourceURL.toString(), new FileInputStream(file));
	}

	private Document parse(String string, InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
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
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_RESOURCE, new InputConnector(
                IN_RESOURCE,
                new MetadataForConnector(IN_RESOURCE.toString(), "Link to OWS XML document"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

}
