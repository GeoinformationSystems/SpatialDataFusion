package de.tudresden.gis.fusion.client.ows.document;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * basic WFS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WFSCapabilities extends OWSCapabilities {
	
	private static final long serialVersionUID = 1L;
	
	private final String WFS_LAYER = ".*(?i)FeatureType";
	
	private Map<String,WFSLayer> wfsLayers;

	/**
	 * Constructor
	 * @param sRequest service request
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WFSCapabilities(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		super(sRequest);
		if(!this.getServiceType().equals(OWSServiceType.WFS))
			throw new IOException("Response is not a WFS capabilities document");
		parseWFSLayer();
	}
	
	/**
	 * parse WFS layers from document
	 */
	private void parseWFSLayer(){
		this.wfsLayers = new HashMap<String,WFSLayer>();
		List<Node> matches = this.getNodes(WFS_LAYER);
		for(Node node : matches) {
			WFSLayer layer = new WFSLayer(node);
			if(layer.getName() != null)
				wfsLayers.put(layer.getName(), layer);
		}
	}
	
	/**
	 * get WFS layer names
	 * @return layer names
	 */
	public Set<String> getWFSLayers(){
		return wfsLayers.keySet();
	}
	
	/**
	 * get center for provided layer as string (x,y), required for OpenLayers
	 * @param layer input layer name
	 * @return center or null, of layer does not exist
	 */
	public String getCenter(String layer) {
		if(wfsLayers.get(layer) == null) 
			return null;
		double[] center = wfsLayers.get(layer).getCenter();
		if(center == null) 
			return null;
		return "[" + center[0] + "," + center[1] + "]";
	}
	
	/**
	 * get extent of layer
	 * @param layer input layer name
	 * @return extent
	 */
	public double[] getExtent(String layer) {
		if(wfsLayers.get(layer) == null) 
			return null;
		return wfsLayers.get(layer).getExtent();
	}
	
	/**
	 * get extent for provided layer as string (minx,miny,maxx,maxy), required for OpenLayers
	 * @param layer input layer name
	 * @return extent or null, of layer does not exist
	 */
	public String getExtentAsString(String layer) {
		double[] extent = getExtent(layer);
		if(extent == null) 
			return null;
		return "[" + String.format(Locale.ENGLISH, "%.8f", extent[0]) + ", " + 
			String.format(Locale.ENGLISH, "%.8f", extent[1]) + ", " + 
			String.format(Locale.ENGLISH, "%.8f", extent[2]) + ", " + 
			String.format(Locale.ENGLISH, "%.8f", extent[3]) + "]";
	}
	
	/**
	 * get srs shorts for selected layer (EPSG:{code}), required for OpenLayers
	 * @param layer input layer name
	 * @return srs shorts or empty set, if layer does not exist or srs cannot be identified
	 */
	public Set<String> getEPSGCodeShorts(String layer) {
		if(wfsLayers.get(layer) == null) 
			return null;
		Set<String> supportedSRS = wfsLayers.get(layer).getSupportedSRS();
		if(supportedSRS == null || supportedSRS.isEmpty())
			return null;
		Set<String> srsShorts = new HashSet<String>();
		for(String srs : supportedSRS){
			String srsShort = getEPSGCodeShort(srs);
			if(srsShort != null)
				srsShorts.add(srsShort);
		}
		return srsShorts;
	}
	
	/**
	 * reduce EPSG code (EPSG:#####)
	 * @param code input code
	 * @return reduced EPSG code
	 */
	private String getEPSGCodeShort(String code){
		String index;
		if(code.contains(":"))
			index = code.substring(code.lastIndexOf(":") + 1);
		else if(code.contains("/"))
			index = code.substring(code.lastIndexOf("/") + 1);
		else return null;
		return "EPSG:" + index;
	}
	
	/**
	 * WFS layer description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class WFSLayer implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String name;
		private Set<String> supportedSRS = new HashSet<String>();
		private String lowerCorner;
		private String upperCorner;
		
		private final String LAYER_NAME = ".*(?i)Name";
		private final String LAYER_CRS = ".*(?i)defaultCRS|defaultSRS|supportedCRS|supportedSRS";
		private final String LAYER_BBOX = ".*(?i)WGS84BoundingBox";
		private final String LAYER_BBOX_LC = ".*(?i)LowerCorner";
		private final String LAYER_BBOX_UC = ".*(?i)UpperCorner";
		
		public WFSLayer(Node layerNode) {
			//get child nodes
			NodeList layerNodes = layerNode.getChildNodes();
			//iterate
			for (int i=0; i<layerNodes.getLength(); i++) {
				//get element node
				Node element = layerNodes.item(i);
				//check for name
				if(element.getNodeName().matches(LAYER_NAME)){
					this.setName(element.getTextContent().trim());
				}
				//check for crs srs
				if(element.getNodeName().matches(LAYER_CRS)) {
					this.addSupportedSRS(element.getTextContent().trim());
				}
				//check for bbox
				if(element.getNodeName().matches(LAYER_BBOX)) {
					NodeList bboxElements = element.getChildNodes();
					for (int j=0; j<bboxElements.getLength(); j++) {
						Node bboxElement = bboxElements.item(j);
						if(bboxElement.getNodeName().matches(LAYER_BBOX_LC))
							this.setLowerCorder(bboxElement.getTextContent().trim());
						else if(bboxElement.getNodeName().matches(LAYER_BBOX_UC))
							this.setUpperCorder(bboxElement.getTextContent().trim());
					}					
				}
			}
		}
		
		public String getName() { return name; }
		public void setName(String name){ this.name = name; }
		
		public Set<String> getSupportedSRS() { return supportedSRS; }
		public void addSupportedSRS(String supportedSRS){ this.supportedSRS.add(supportedSRS); }		

		public String getLowerCorner() { return lowerCorner; }
		public void setLowerCorder(String lc) { this.lowerCorner = lc; }
		
		public String getUpperCorner() { return upperCorner; }
		public void setUpperCorder(String uc) { this.upperCorner = uc; }
		
		/**
		 * get extent [minx,miny,maxx,maxy]
		 * @return extent
		 */
		public double[] getExtent() {
			if(lowerCorner == null || upperCorner == null)
				return null;
			String[] aLc = this.getLowerCorner().split(" ");
			String[] aUc = this.getUpperCorner().split(" ");
			return new double[]{Double.parseDouble(aLc[0]), Double.parseDouble(aLc[1]), Double.parseDouble(aUc[0]), Double.parseDouble(aUc[1])};
		}
		
		/**
		 * get center coordinate for layer
		 * @return center coordinate
		 */
		public double[] getCenter() {
			double[] extent = this.getExtent();
			if(extent == null) 
				return null;
			return new double[]{((extent[0] + extent[2]) / 2), ((extent[1] + extent[3]) / 2)};
		}
		
	}

}
