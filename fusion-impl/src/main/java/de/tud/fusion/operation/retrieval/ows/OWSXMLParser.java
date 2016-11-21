package de.tud.fusion.operation.retrieval.ows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.InputConnector;

public abstract class OWSXMLParser extends AbstractOperation {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	
	private Set<IInputConnector> inputConnectors;

	/**
	 * constructor
	 * @param identifier process identifier
	 */
	public OWSXMLParser(String identifier) {
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
	
	public String getDocumentIdentifier() {
		return getResourceURL().toString();
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
			throw new IOException("OWS resource is not valid or not accessible");
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
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_RESOURCE, IN_RESOURCE, "Link to OWS XML document",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null,
				null));	
		//return
		return inputConnectors;
	}

}
