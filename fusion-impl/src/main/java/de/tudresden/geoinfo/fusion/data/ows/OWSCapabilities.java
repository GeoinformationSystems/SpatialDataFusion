package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.w3c.dom.Document;

import java.io.IOException;

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
	public OWSCapabilities(IIdentifier identifier, Document object, IMetadataForData description) {
		super(identifier, object, description);
		initCapabilities();
	}
	
	/**
	 * parse capabilities default
	 * @throws IOException
	 */
	private void initCapabilities() {
		setServiceIdentification(resolve());
	}
	
	/**
	 * parse service identification
	 * @param document input document
	 */
	private void setServiceIdentification(Document document) {
		if(document.getFirstChild().getAttributes().getNamedItem("xsi:schemaLocation") == null)
            throw new IllegalArgumentException("OWS schemaLocation is undefined");
		String schemaLocation = document.getFirstChild().getAttributes().getNamedItem("xsi:schemaLocation").getNodeValue();
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
			throw new IllegalArgumentException("OWS schemaLocation is unknown");
	}

	/**
	 * get service type
	 * @return one of OWSServiceType
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
