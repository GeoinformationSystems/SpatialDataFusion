package de.tudresden.gis.fusion.client.ows;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import de.tudresden.gis.fusion.client.ows.document.WFSCapabilities;

public class WFSHandler extends OWSHandler {

	private static final long serialVersionUID = 1L;
	
	private final String SERVICE = "wfs";
	
	private final String REQUEST_DESCRIBEFEATURETYPE = "describeFeatureType";
	private final String REQUEST_GETFEATURE = "getFeature";

	private final String PARAM_TYPENAME = "typename";
	private final String PARAM_OUTPUTFORMAT = "outputformat";
	private final String PARAM_SRSNAME = "srsname";
	private final String PARAM_BBOX = "bbox";
	
	//flag: force WGS84 as default crs
	private final String DEFAULT_CRS_WGS84 = "EPSG:4326";
	private boolean forceWGS84 = false;
	public boolean getForceWGS84() { return this.forceWGS84; }
	public void setForceWGS84(boolean forceWGS84) { 
		this.forceWGS84 = forceWGS84;
		if(forceWGS84)
			this.setSRSName(DEFAULT_CRS_WGS84);
	}
	
	//supported versions
	private final String VERSION_100 = "1.0.0";
	private final String VERSION_110 = "1.1.0";
	private final String VERSION_200 = "2.0.0";
	private final String VERSION_202 = "2.0.2";
	public String[] getSupportedVersions() { return new String[]{VERSION_100,VERSION_110,VERSION_200,VERSION_202}; }
	
	private final String DEFAULT_VERSION = VERSION_110;
//	private final String DEFAULT_OUTPUTFORMAT = "application/json";
	
	@PostConstruct
	public void init(){
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
//		this.setOutputFormat(DEFAULT_OUTPUTFORMAT);
	}
	
	/**
	 * get describeFeatureType request for selected typename
	 * @return describeFeatureType request
	 * @throws IOException
	 */
	public String getDescribeFeatureTypeRequest() throws IOException {
		if(this.getWFSBaseIsInvalid()) return null;
		this.setRequest(REQUEST_DESCRIBEFEATURETYPE);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{});
	}
	
	/**
	 * get describeFeatureType request for sleected typename
	 * @return describeFeatureType request
	 * @throws IOException
	 */
	public String getGetFeatureRequest() throws IOException {
		if(this.getWFSBaseIsInvalid()) return null;
		this.setRequest(REQUEST_GETFEATURE);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{PARAM_OUTPUTFORMAT,PARAM_SRSNAME,PARAM_BBOX});
	}
	
	/**
	 * check if WFS base is valid
	 * @return true, if base is valid
	 */
	public boolean getWFSBaseIsInvalid() {
		if(!this.validOWSBase()) return true;
		if(this.getTypename() == null || this.getTypename().length() == 0) return true;
		return false;
	}
	
	WFSCapabilities capabilities;
	public void initCapabilities() {
		//retrieve layer information from WFS
		try {
			//get capabilities document
			capabilities = new WFSCapabilities(this.getGetCapabilitiesRequest());
		} catch (Exception e) {
			//display error message and return
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load capabilities from server (" + e.getLocalizedMessage() + ")");
			return;
		}
		//display success message
		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "loaded capabilities from server");
	}
	
	public Set<String> getSupportedTypenames() {
		if(capabilities != null)
			return capabilities.getWFSLayers();
		return null;
	}

	public Set<String> getSupportedSRS() {
		if(this.getTypename() == null || capabilities == null) 
			return null;
		return capabilities.getEPSGCodeShorts(getTypename());
	}
	
	//get layer center (for OL)
	public String getCenterAsString() {
		double[] extent = this.getExtent();
		if(extent == null)
			return null;
		return "[" + ((extent[0] + extent[2]) / 2) + "," + ((extent[1] + extent[3]) / 2) + "]";
	}
	
	//get extent minx, miny, maxx, maxy
	public double[] getExtent() {
		if(this.getTypename() == null || capabilities == null) 
			return null;
		return capabilities.getExtent(getTypename());
	}
	
	//get extent minx, miny, maxx, maxy (LOCALE.English)
	public String getExtentAsString() {
		if(this.getTypename() == null || capabilities == null) 
			return null;
		return capabilities.getExtentAsString(getTypename());
	}
	
	public String getTypename() { return this.getParameter(PARAM_TYPENAME); }
	public void setTypename(String value) { this.setParameter(PARAM_TYPENAME, value); }
	
	public String getOutputFormat() { return this.getParameter(PARAM_OUTPUTFORMAT); }
	public void setOutputFormat(String value) { this.setParameter(PARAM_OUTPUTFORMAT, value); }

	public String getSRSName() { return this.getParameter(PARAM_SRSNAME); }
	public void setSRSName(String value) { this.setParameter(PARAM_SRSNAME, value); }
	
//	public String getOlSRSName() {
//		String srs = getSRSName();
//		if(srs == null || srs.length() == 0 || !srs.toLowerCase().contains("epsg"))
//			return null;
//		String[] srsSplit = srs.split(":");
//		return "EPSG:" + srsSplit[srsSplit.length - 1]; 
//	}

	public String getBBox() { return this.getParameter(PARAM_BBOX); }
	public void setBBox(String value) { this.setParameter(PARAM_BBOX, value); }
	
}
