package de.tudresden.gis.fusion.client.ows.document;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * basic OWS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OWSCapabilities extends OWSResponse {
	
	private static final long serialVersionUID = 1L;
	
	private final String SERVICE_IDENTIFICATION = ".*(?i)ServiceIdentification";
	private final String SERVICE_IDENTIFICATION_SERVICE_TYPE = ".*(?i)ServiceType";

	private OWSServiceType serviceType;
	
	/**
	 * Constructor
	 * @param sRequest service request string
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public OWSCapabilities(String sRequest) throws ParserConfigurationException, SAXException, IOException{
		super(sRequest);
		parseDefault();
	}
	
	/**
	 * parse capabilities default
	 * @throws IOException
	 */
	private void parseDefault() throws IOException {
		parseServiceIdentification(getNode(SERVICE_IDENTIFICATION));
		//TODO: add other service capability properties
	}
	
	/**
	 * parse service identification
	 * @param serviceIdentification input node
	 * @throws IOException
	 */
	private void parseServiceIdentification(Node serviceIdentification) throws IOException{
		if(serviceIdentification == null)
			throw new IOException("ServiceIdentification must not be null");
		NodeList elements = serviceIdentification.getChildNodes();
		for (int i=0; i<elements.getLength(); i++) {
			Node element = elements.item(i);
			if(element.getNodeName().matches(SERVICE_IDENTIFICATION_SERVICE_TYPE)){
				serviceType = OWSServiceType(element.getTextContent().trim());
			}
			//TODO: add other service identification properties
		}
		if(serviceType == null)
			throw new IOException("ServiceType must not be null");
	}
	
	/**
	 * get service type based on input type string
	 * @param type type string from capabilities document
	 * @return service type enumeration
	 */
	private OWSServiceType OWSServiceType(String type) {
		if(type.toUpperCase().contains("WPS"))
			return OWSServiceType.WPS;
		else if(type.toUpperCase().contains("WFS"))
			return OWSServiceType.WFS;
		else if(type.toUpperCase().contains("WCS"))
			return OWSServiceType.WCS;
		else if(type.toUpperCase().contains("WMS"))
			return OWSServiceType.WMS;
		return null;
	}

	/**
	 * get service type
	 * @return
	 */
	public OWSServiceType getServiceType(){
		return serviceType;
	}
	
	/**
	 * service types enumeration
	 */
	public enum OWSServiceType {
		WFS, WPS, WMS, WCS
	}
	
}
