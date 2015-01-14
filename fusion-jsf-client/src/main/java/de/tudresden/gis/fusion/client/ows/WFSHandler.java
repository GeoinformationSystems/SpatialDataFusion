package de.tudresden.gis.fusion.client.ows;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WFSHandler extends OWSHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<WFSLayer> layers = new HashSet<WFSLayer>();
	
	private final String SERVICE = "wfs";
	
	private final String REQUEST_DESCRIBEFEATURETYPE = "describeFeatureType";
	private final String REQUEST_GETFEATURE = "getFeature";

	private final String PARAM_TYPENAME = "typename";
	private final String PARAM_OUTPUTFORMAT = "outputformat";
	private final String PARAM_SRSNAME = "srsname";
	private final String PARAM_BBOX = "bbox";
	
	//flag: force WGS84 as default crs
	private final String DEFAULT_CRS_WGS84 = "epsg:4326";
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
	private final String DEFAULT_OUTPUTFORMAT = "application/json";
	
	@PostConstruct
	public void init(){
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
		this.setOutputFormat(DEFAULT_OUTPUTFORMAT);
	}
	
	/**
	 * get describeFeatureType request for sleected typename
	 * @return describeFeatureType request
	 * @throws IOException
	 */
	public String getDescribeFeatureTypeRequest() throws IOException {
		if(!this.validWFSBase()) return null;
		this.setRequest(REQUEST_DESCRIBEFEATURETYPE);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{});
	}
	
	/**
	 * get describeFeatureType request for sleected typename
	 * @return describeFeatureType request
	 * @throws IOException
	 */
	public String getGetFeatureRequest() throws IOException {
		if(!this.validWFSBase()) return null;
		this.setRequest(REQUEST_GETFEATURE);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_TYPENAME}, new String[]{PARAM_OUTPUTFORMAT,PARAM_SRSNAME,PARAM_BBOX});
	}
	
	private boolean validWFSBase() {
		if(!this.validOWSBase()) return false;
		if(this.getTypename() == null || this.getTypename().length() == 0) return false;
		return true;
	}
	
	public boolean getWfsBaseInvalid() {
		return !validWFSBase();
	}
	
	/**
	 * init WFS capabilities
	 */
	public void initCapabilities() {
		//remove layer
		layers.clear();
		//retrieve layer information from WFS
		try {
			//get capabilities document
			Document capabilities = this.getCapabilities();
			//get layers
			List<Node> matches = this.getElementsByRegEx(capabilities.getChildNodes(), ".*(?i)FeatureType", new ArrayList<Node>());
			for(Node node : matches) {
				WFSLayer layer = new WFSLayer(node);
				if(layer.getName() != null)
					layers.add(layer);
			}
		} catch (Exception e) {
			//display error message and return
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load capabilities from server (" + e.getLocalizedMessage() + ")");
			return;
		}
		//display success message
		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "loaded capabilities from server");
	}
	
	public List<String> getSupportedTypenames() { 
		List<String> typenames = new ArrayList<String>();
		for(WFSLayer layer : layers){
			typenames.add(layer.getName());
		}
		return typenames;
	}

	public List<String> getSupportedSRS() {
		if(this.getTypename() == null) 
			return null;
		for(WFSLayer layer : layers){
			if(layer.getName().equalsIgnoreCase(this.getTypename()))
				return layer.getSupportedSRS();
		}
		return null;
	}
	
	//get layer center (for OL)
	public String getCenter() {
		double[] extent = this.getExtent();
		if(extent == null)
			return null;
		return "[" + ((extent[0] + extent[2]) / 2) + "," + ((extent[1] + extent[3]) / 2) + "]";
	}
	
	//get extent minx, miny, maxx, maxy
	public double[] getExtent() {
		if(this.getSelectedLayer() == null) 
			return null;
		return this.getSelectedLayer().getExtent();
	}
	
	/**
	 * get layer by selected typename
	 * @return layer with selected typename or null, if no typename is selected
	 */
	private WFSLayer getSelectedLayer(){
		if(this.getTypename() == null) 
			return null;
		for(WFSLayer layer : layers){
			if(layer.getName().equalsIgnoreCase(this.getTypename()))
				return layer;
		}
		return null;
	}
	
	/**
	 * get center of selected layer as string
	 * @return layer center
	 */
	public String getOlCenter(){
		WFSLayer layer = getSelectedLayer();
		if(layer == null) return null;
		else return layer.getCenterXY();
	}
	
	/**
	 * get extent of selected layer as string
	 * @return layer extent
	 */
	public String getOlExtent(){
		WFSLayer layer = getSelectedLayer();
		if(layer == null) return null;
		else return layer.getExtentX1Y1X2Y2();
	}
	
	public String getTypename() { return this.getParameter(PARAM_TYPENAME); }
	public void setTypename(String value) { this.setParameter(PARAM_TYPENAME, value); }
	
	public String getOutputFormat() { return this.getParameter(PARAM_OUTPUTFORMAT); }
	public void setOutputFormat(String value) { this.setParameter(PARAM_OUTPUTFORMAT, value); }

	public String getSRSName() { return this.getParameter(PARAM_SRSNAME); }
	public void setSRSName(String value) { this.setParameter(PARAM_SRSNAME, value); }
	
	public String getOlSRSName() {
		String srs = getSRSName();
		if(srs == null || srs.length() == 0 || !srs.toLowerCase().contains("epsg"))
			return null;
		String[] srsSplit = srs.split(":");
		return "EPSG:" + srsSplit[srsSplit.length - 1]; 
	}

	public String getBBox() { return this.getParameter(PARAM_BBOX); }
	public void setBBox(String value) { this.setParameter(PARAM_BBOX, value); }
	
	/**
	 * wfs layer description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class WFSLayer implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String name;
		private List<String> supportedSRS = new ArrayList<String>();
		private String lowerCorner;
		private String upperCorner;
		
		public WFSLayer(Node layerNode) {
			//get child nodes
			NodeList layerNodes = layerNode.getChildNodes();
			//iterate
			for (int i=0; i<layerNodes.getLength(); i++) {
				//get element node
				Node element = layerNodes.item(i);
				//check for name
				if(element.getNodeName().matches(".*(?i)Name")){
					this.setName(element.getTextContent().trim());
				}
				//check for crs srs
				if(element.getNodeName().matches(".*(?i)defaultCRS|defaultSRS|supportedCRS|supportedSRS")) {
					this.addSupportedSRS(element.getTextContent().trim());
				}
				//check for bbox
				if(element.getNodeName().matches(".*(?i)WGS84BoundingBox")) {
					NodeList bboxElements = element.getChildNodes();
					for (int j=0; j<bboxElements.getLength(); j++) {
						Node bboxElement = bboxElements.item(j);
						if(bboxElement.getNodeName().matches(".*(?i)LowerCorner"))
							this.setLowerCorder(bboxElement.getTextContent().trim());
						else if(bboxElement.getNodeName().matches(".*(?i)UpperCorner"))
							this.setUpperCorder(bboxElement.getTextContent().trim());
					}					
				}
			}
		}
		
		public String getName() { return name; }
		public void setName(String name){ this.name = name; }
		
		public List<String> getSupportedSRS() { return supportedSRS; }
		public void addSupportedSRS(String supportedSRS){ this.supportedSRS.add(supportedSRS); }		

		public String getLowerCorner() { return lowerCorner; }
		public void setLowerCorder(String lc) { this.lowerCorner = lc; }
		
		public String getUpperCorner() { return upperCorner; }
		public void setUpperCorder(String uc) { this.upperCorner = uc; }
		
		public double[] getExtent() {
			if(lowerCorner == null || upperCorner == null)
				return null;
			String[] aLc = this.getLowerCorner().split(" ");
			String[] aUc = this.getUpperCorner().split(" ");
			return new double[]{Double.parseDouble(aLc[0]), Double.parseDouble(aLc[1]), Double.parseDouble(aUc[0]), Double.parseDouble(aUc[1])};
		}
		
		//get layer center as String (for OL)
		public String getCenterXY() {
			double[] extent = this.getExtent();
			if(extent == null) return null;
			return "[" + ((extent[0] + extent[2]) / 2) + "," + ((extent[1] + extent[3]) / 2) + "]";
		}
		
		//get extent as String (minx, miny, maxx, maxy; for OL)
		public String getExtentX1Y1X2Y2() {
			double[] extent = this.getExtent();
			if(extent == null) return null;
			return "[" + String.format(Locale.ENGLISH, "%.4f", extent[0]) + ", " + 
				String.format(Locale.ENGLISH, "%.4f", extent[1]) + ", " + 
				String.format(Locale.ENGLISH, "%.4f", extent[2]) + ", " + 
				String.format(Locale.ENGLISH, "%.4f", extent[3]) + "]";
		}
		
	}
	
}
