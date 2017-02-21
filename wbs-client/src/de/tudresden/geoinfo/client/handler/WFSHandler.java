package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.operation.retrieval.GMLParser;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WFSHandler extends OWSHandler {

    private final static String REQUEST_GETFEATURE = "getFeature";
    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
    private final static IIdentifier IN_WITH_INDEX = new Identifier("IN_WITH_INDEX");
    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
    private final String SERVICE = "WFS";
    private final String DEFAULT_VERSION = "1.0.0";
    private final String PARAM_TYPENAME = "typename";
    private final String PARAM_OUTPUTFORMAT = "outputformat";
    private final String PARAM_SRSNAME = "srsname";
    private final String PARAM_BBOX = "bbox";
    private final String PARAM_IDENTIFIER = "featureID";
    private WFSCapabilities wfsCapabilities;

    /**
     * constructor
     *
     * @param sBaseURL WFS base url
     * @throws IOException
     */
    public WFSHandler(String sBaseURL) throws IOException {
        super(sBaseURL);
        this.setService(SERVICE);
        this.setVersion(DEFAULT_VERSION);
        this.wfsCapabilities = getCapabilities();
    }

    /**
     * get WFS capabilities document
     *
     * @return capabilites document
     */
    public WFSCapabilities getCapabilities() throws IOException {
        OWSCapabilities capabilities = super.getCapabilities();
        if (!(capabilities instanceof WFSCapabilities))
            throw new IOException("Could not parse WFS capabilities");
        return (WFSCapabilities) capabilities;
    }

    /**
     * get all layers provided by this WFS instance
     *
     * @return WFS layers
     */
    public Set<String> getSupportedLayers() {
        return wfsCapabilities != null ? wfsCapabilities.getWFSLayers() : Collections.emptySet();
    }

    /**
     * check, if certain layer is provided
     *
     * @param sLayer input layer name
     * @return true, if layer is provided by WMS
     */
    public boolean isSupportedLayer(String sLayer) {
        return this.getSupportedLayers().contains(sLayer);
    }

    /**
     * get selected WFS layer
     *
     * @return selected WFS layer
     */
    public String getLayer() {
        return this.getParameter(PARAM_TYPENAME);
    }

    /**
     * select WFS layer
     *
     * @param sLayer WFS layer name
     */
    public void setLayer(String sLayer) {
        if (!this.isSupportedLayer(sLayer))
            throw new IllegalArgumentException("Layer " + sLayer + " is not supported");
        this.setParameter(PARAM_TYPENAME, sLayer);
    }

    /**
     * get features from WFS
     */
    public GTFeatureCollection getFeatures(Set<String> identifier) throws IOException {
        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(URI.create(getGetFeatureRequest(identifier))));
        input.put(IN_WITH_INDEX, new BooleanLiteral(true));
        GMLParser parser = new GMLParser();
        Map<IIdentifier, IData> output = parser.execute(input);
        return (GTFeatureCollection) output.get(OUT_FEATURES);
    }

    /**
     * get WFS getFeature request
     */
    public String getGetFeatureRequest(Set<String> identifier) throws IOException {
        this.setRequest(REQUEST_GETFEATURE);
        if (!identifier.isEmpty())
            this.setParameter(PARAM_IDENTIFIER, String.join(",", identifier));
        return getKVPRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST, PARAM_VERSION, PARAM_TYPENAME}, new String[]{PARAM_IDENTIFIER, PARAM_OUTPUTFORMAT, PARAM_SRSNAME, PARAM_BBOX});
    }

}

//
//public class WFSHandler extends OWSHandler {
//
//	private static final long serialVersionUID = 1L;
//
//	private final String SERVICE = "WFS";
//
//	private final String REQUEST_DESCRIBEFEATURETYPE = "describeFeatureType";
//	private final String REQUEST_GETFEATURE = "getFeature";

//
//	//flag: force WGS84 as default crs
//	private final String DEFAULT_CRS_WGS84 = "EPSG:4326";
//	private boolean forceWGS84 = false;
//	public boolean getForceWGS84() { return this.forceWGS84; }
//	public void setForceWGS84(boolean forceWGS84) {
//		this.forceWGS84 = forceWGS84;
//		if(forceWGS84)
//			this.setSRSName(DEFAULT_CRS_WGS84);
//	}
//
//	//supported versions
//	private final String VERSION_100 = "1.0.0";
//	private final String VERSION_110 = "1.1.0";
//	private final String VERSION_200 = "2.0.0";
//	private final String VERSION_202 = "2.0.2";
//	public String[] getSupportedVersions() { return new String[]{VERSION_100,VERSION_110,VERSION_200,VERSION_202}; }
//
//	private final String DEFAULT_VERSION = VERSION_110;
////	private final String DEFAULT_OUTPUTFORMAT = "application/json";
//
//	@PostConstruct
//	public void init(){
//		this.setService(SERVICE);
//		this.setVersion(DEFAULT_VERSION);
////		this.setOutputFormat(DEFAULT_OUTPUTFORMAT);
//	}
//
//	*/
/**
 * //	 * get describeFeatureType request for selected typename
 * //	 * @return describeFeatureType request
 * //	 * @throws IOException
 * //
 * //	 * get describeFeatureType request for sleected typename
 * //	 * @return describeFeatureType request
 * //	 * @throws IOException
 * //
 * //	 * check if WFS base is valid
 * //	 * @return true, if base is valid
 * //
 * //	 * get WFS layer as IOProcess for chaining purposes
 * //	 * @return io process
 * //	 * @throws IOException
 * //
 *//*

//	public String getDescribeFeatureTypeRequest() throws IOException {
//		if(this.getWFSBaseIsInvalid()) return null;
//		this.setRequest(REQUEST_DESCRIBEFEATURETYPE);
//		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{});
//	}
//
//	*/
/**
 //	 * get describeFeatureType request for sleected typename
 //	 * @return describeFeatureType request
 //	 * @throws IOException
 //	 *//*

//	public String getGetFeatureRequest() throws IOException {
//		if(this.getWFSBaseIsInvalid()) return null;
//		this.setRequest(REQUEST_GETFEATURE);
//		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{PARAM_OUTPUTFORMAT,PARAM_SRSNAME,PARAM_BBOX});
//	}
//
//	*/
/**
 //	 * check if WFS base is valid
 //	 * @return true, if base is valid
 //	 *//*

//	public boolean getWFSBaseIsInvalid() {
//		if(!this.validOWSBase()) return true;
//		if(this.getTypename() == null || this.getTypename().length() == 0) return true;
//		return false;
//	}
//
//	WFSCapabilities capabilities;
//	public void initCapabilities() {
//		//retrieve layer information from WFS
//		try {
//			//get capabilities document
//			capabilities = new WFSCapabilities(this.getGetCapabilitiesRequest());
//		} catch (Exception e) {
//			//display error message and return
//			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load capabilities from server (" + e.getLocalizedMessage() + ")");
//			return;
//		}
//		//display success message
//		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "loaded capabilities from server");
//	}
//
//	public Set<String> getSupportedTypenames() {
//		if(capabilities != null)
//			return capabilities.getWFSLayers();
//		return null;
//	}
//
//	public Set<String> getSupportedSRS() {
//		if(this.getTypename() == null || capabilities == null)
//			return null;
//		return capabilities.getEPSGCodeShorts(getTypename());
//	}
//
//	//get layer center (for OL)
//	public String getCenterAsString() {
//		double[] extent = this.getExtent();
//		if(extent == null)
//			return null;
//		return "[" + ((extent[0] + extent[2]) / 2) + "," + ((extent[1] + extent[3]) / 2) + "]";
//	}
//
//	//get extent minx, miny, maxx, maxy
//	public double[] getExtent() {
//		if(this.getTypename() == null || capabilities == null)
//			return null;
//		return capabilities.getExtent(getTypename());
//	}
//
//	//get extent minx, miny, maxx, maxy (LOCALE.English)
//	public String getExtentAsString() {
//		if(this.getTypename() == null || capabilities == null)
//			return null;
//		return capabilities.getExtentAsString(getTypename());
//	}
//
//	public String getTypename() { return this.getParameter(PARAM_TYPENAME); }
//	public void setTypename(String value) { this.setParameter(PARAM_TYPENAME, value); }
//
//	public String getOutputFormat() { return this.getParameter(PARAM_OUTPUTFORMAT); }
//	public void setOutputFormat(String value) { this.setParameter(PARAM_OUTPUTFORMAT, value); }
//
//	public String getSRSName() { return this.getParameter(PARAM_SRSNAME); }
//	public void setSRSName(String value) { this.setParameter(PARAM_SRSNAME, value); }
//
////	public String getOlSRSName() {
////		String srs = getSRSName();
////		if(srs == null || srs.length() == 0 || !srs.toLowerCase().contains("epsg"))
////			return null;
////		String[] srsSplit = srs.split(":");
////		return "EPSG:" + srsSplit[srsSplit.length - 1];
////	}
//
//	public String getBBox() { return this.getParameter(PARAM_BBOX); }
//	public void setBBox(String value) { this.setParameter(PARAM_BBOX, value); }
//
//	*/
/**
 //	 * get WFS layer as IOProcess for chaining purposes
 //	 * @return io process
 //	 * @throws IOException
 //	 *//*

//	public IOProcess getIOProcess() throws IOException{
//		WPSIOFormat defaultFormat = new WPSIOFormat("text/xml", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd", "");
//		Set<WPSIOFormat> supportedFormats = new HashSet<WPSIOFormat>();
//		supportedFormats.add(defaultFormat);
//		supportedFormats.add(new WPSIOFormat("application/json", "", ""));
//		IONode node = new IONode(null, "OUT_FEATURES", defaultFormat, supportedFormats, NodeType.OUTPUT);
//		Map<String,String> properties = new HashMap<String,String>();
//		properties.put("base", this.getBaseURL());
//		properties.put(PARAM_SRSNAME, this.getSRSName());
//		properties.put(PARAM_TYPENAME, this.getTypename());
//		properties.put(PARAM_BBOX, this.getBBox());
//		IOProcess process = new IOProcess(SERVICE, this.getUUID(), properties, node);
//		return process;
//	}
//
//	private String getUUID() {
//		if(this instanceof ReferenceWFS)
//			return "0_ReferenceWFS";
//		if(this instanceof TargetWFS)
//			return "0_TargetWFS";
//
//		return null;
//	}
//
//}
*/
