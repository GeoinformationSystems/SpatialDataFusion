package de.tud.fusion.data.ows;

import java.io.IOException;

import org.w3c.dom.Document;
import de.tud.fusion.data.description.IDataDescription;

/**
 * standard OWS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OWSCapabilities extends OWSResponse {
	
	private OWSServiceType serviceType;
	
	/**
	 * constructor
	 * @param identifier OWS capabilities identifier
	 * @param object OWS capabilities document
	 * @param description OWS capabilities description
	 */
	public OWSCapabilities(String identifier, Document object, IDataDescription description) {
		super(identifier, object, description);
		initCapabilities();
	}
	
	/**
	 * parse capabilities default
	 * @throws IOException
	 */
	private void initCapabilities() {
		try {
			setServiceIdentification(resolve());
			//TODO add additional properties
		} catch (IOException e) {
			throw new IllegalArgumentException("OWS response is not a valid capabilities document", e);
		}
	}
	
	/**
	 * parse service identification
	 * @param serviceIdentification input node
	 * @throws IOException
	 */
	private void setServiceIdentification(Document document) throws IOException {
		String schemaLocation = document.getFirstChild().getAttributes().getNamedItem("xsi:schemaLocation").getNodeValue();
		if(schemaLocation == null)
			throw new IOException("OWS schemaLocation must not be null");
		if(schemaLocation.contains("www.opengis.net/wps"))
			this.serviceType = OWSServiceType.WPS;
		else if(schemaLocation.contains("www.opengis.net/wfs"))
			this.serviceType = OWSServiceType.WFS;
		else if(schemaLocation.contains("www.opengis.net/wcs"))
			this.serviceType = OWSServiceType.WCS;
		else if(schemaLocation.contains("www.opengis.net/wms"))
			this.serviceType = OWSServiceType.WMS;
		else if(schemaLocation.contains("www.opengis.net/sos"))
			this.serviceType = OWSServiceType.SOS;
		else
			throw new IllegalArgumentException("OWS type cannot be identified");
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
	public static enum OWSServiceType {
		WFS, WPS, WMS, WCS, SOS
	}
	
}
