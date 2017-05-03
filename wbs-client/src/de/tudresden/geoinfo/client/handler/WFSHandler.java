package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class WFSHandler extends AbstractOWSHandler {

    private JSONArray selectedFeatures;

    /**
     * constructor
     *
     * @param sBaseURL WFS base url
     * @throws MalformedURLException
     */
    public WFSHandler(String uid, String sBaseURL) throws IOException {
        super(new WFSProxy(new Identifier(uid), new URLLiteral(sBaseURL)));
    }

    @Override
    public WFSCapabilities getCapabilities() {
        return (WFSCapabilities) super.getCapabilities();
    }

    @Override
    @NotNull
    public WFSProxy getProxy() {
        return (WFSProxy) super.getProxy();
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWFSLayers();
    }

    @Override
    public @Nullable String getSelectedOffering() {
        return this.getProxy().getTypename();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        this.getProxy().setTypename(offering);
    }

   /* *//**
     * get WFS request
     * @param sIdentifier feature identifier
     * @return corresponding WFS getFeature request
     * @throws MalformedURLException
     *//*
    public URLLiteral getRequest(@Nullable String sIdentifier) throws MalformedURLException {
        this.getProxy().setFeatureIdentifier(sIdentifier.equals(FID_ALL) ? null : sIdentifier);
        return this.getProxy().getFeatureRequest();
    }

    *//**
     * get WFS request
     * @return corresponding WFS getFeature request
     * @throws MalformedURLException
     *//*
    public URLLiteral getRequest() throws MalformedURLException {
        return this.getRequest(null);
    }*/

    /**
     * set feature selection
     * @param selectedFeatures selected features
     */
    public void setSelectedFeatures(@Nullable JSONArray selectedFeatures){
        this.selectedFeatures = selectedFeatures;
        this.setFeatureIdentifier();
    }

    /**
     * get feature selection
     * @return selected features
     */
    public @NotNull JSONArray getSelectedFeatures() {
        return this.selectedFeatures != null ? this.selectedFeatures : new JSONArray();
    }

    /**
     * get selected feature identifier
     * @return selected feature identifier
     */
    public @NotNull Set<String> getSelectedIdentifier() {
        Set<String> fids = new HashSet<>();
        for(Object featureObject : this.getSelectedFeatures()) {
            fids.add(((JSONObject) featureObject).getString("id"));
        }
        return fids;
    }

    /**
     * get features from WFS
     */
    public GTFeatureCollection getFeatures() throws IOException {
        return this.getProxy().getFeatures();
    }

    /**
     * set selected feature identifier,
     */
    public void setFeatureIdentifier() {
        this.getProxy().setFeatureIdentifier(getSelectedIdentifier());
    }

    /**
     * get WFS input description as json (used by jsPlumb)
     *
     * @return JSON process description
     */
    public JSONObject getJSONDescription() {
        if (this.getSelectedOffering() == null)
            return null;
        //set hidden entries
        Set<IIdentifier> hiddenInputs = new HashSet<>();
        hiddenInputs.add(this.getProxy().getInputConnector("IN_FORMAT").getIdentifier());
        hiddenInputs.add(this.getProxy().getInputConnector("IN_LAYER").getIdentifier());
        hiddenInputs.add(this.getProxy().getInputConnector("IN_FID").getIdentifier());
        Set<IIdentifier> hiddenOutputs = new HashSet<>();
        hiddenOutputs.add(this.getProxy().getOutputConnector("OUT_START").getIdentifier());
        hiddenOutputs.add(this.getProxy().getOutputConnector("OUT_RUNTIME").getIdentifier());
        //get description
        return JSONUtils.getJSONDescription(this.getProxy(), hiddenInputs, hiddenOutputs);


        /*if (this.getSelectedOffering() == null)
            return null;
        JSONArray outputs = new JSONArray();
        //init selected features
        Set<String> identifiers = this.getSelectedIdentifier();
        if(identifiers.isEmpty())
            outputs.put(getJSONDescription(this.getSelectedOffering(), FID_ALL));
        else {
            for(String identifier : identifiers){
                outputs.put(getJSONDescription(this.getSelectedOffering(), identifier));
            }
        }
        return new JSONObject()
                .put("identifier", this.getProxy().getIdentifier().toString())
                .put("title", this.getSelectedOffering())
                .put("description", this.getSelectedOffering())
                .put("inputs", new JSONArray())
                .put("outputs", outputs);*/
    }

    /**
     * get process io description as json
     *
     * @param sLayer selected layer
     * @param sIdentifier selected fid
     * @return JSON io process description
     */
    private JSONObject getJSONDescription(String sLayer, String sIdentifier) {
        return new JSONObject()
                .put("identifier", sIdentifier + "@" + sLayer)
                .put("minOccurs", 1)
                .put("maxOccurs", 1)
                .put("title", sIdentifier)
                .put("defaultFormat", JSONUtils.getJSONDescription(this.getCapabilities().getOutputDescription().getDefaultFormat()))
                .put("supportedFormats", JSONUtils.getJSONDescription(this.getCapabilities().getOutputDescription().getSupportedFormats()));
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
//		IOFormat defaultFormat = new IOFormat("text/xml", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd", "");
//		Set<IOFormat> supportedFormats = new HashSet<IOFormat>();
//		supportedFormats.add(defaultFormat);
//		supportedFormats.add(new IOFormat("application/json", "", ""));
//		IONode node = new IONode(null, "OUT_FEATURES", defaultFormat, supportedFormats, NodeType.OUTPUT);
//		Map<String,String> properties = new HashMap<String,String>();
//		properties.put("base", this.getBase());
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
