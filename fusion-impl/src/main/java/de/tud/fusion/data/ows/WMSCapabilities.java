package de.tud.fusion.data.ows;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tud.fusion.data.description.IDataDescription;

/**
 * standard WFS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WMSCapabilities extends OWSCapabilities {
	
	private final String WMS_LAYER = ".*(?i)Layer";
	
	private Map<String,WMSLayer> wmsLayers;

	/**
	 * Constructor
	 * @param sRequest service request
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WMSCapabilities(String identifier, Document object, IDataDescription description) throws ParserConfigurationException, SAXException, IOException {
		super(identifier, object, description);
		if(!this.getServiceType().equals(OWSServiceType.WMS))
			throw new IOException("Document is not a WMS capabilities document");
		initWMSCapabilities();
	}
	
	/**
	 * Constructor
	 * @param owsCapabilities input capabilities
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public WMSCapabilities(OWSCapabilities owsCapabilities) throws ParserConfigurationException, SAXException, IOException {
		this(owsCapabilities.getIdentifier(), owsCapabilities.resolve(), owsCapabilities.getDescription());
	}

	/**
	 * initialize WFS capabilities
	 */
	private void initWMSCapabilities(){
		this.wmsLayers = new HashMap<String,WMSLayer>();
		List<Node> matches = this.getNodes(WMS_LAYER);
		for(Node node : matches) {
			WMSLayer layer = new WMSLayer(node);
			if(layer.getName() != null)
				wmsLayers.put(layer.getName(), layer);
		}
	}
	
	/**
	 * get WFS layer names
	 * @return layer names
	 */
	public Set<String> getWMSLayers(){
		return wmsLayers.keySet();
	}
	
	/**
	 * get center for provided layer as string (x,y), required for OpenLayers
	 * @param layer input layer name
	 * @return center or null, of layer does not exist
	 */
	public String getCenter(String layer) {
		if(wmsLayers.get(layer) == null) 
			return null;
		double[] center = wmsLayers.get(layer).getCenter();
		if(center == null) 
			return null;
		return "[" + center[0] + "," + center[1] + "]";
	}
	
	/**
	 * get extent of layer [west,south,east,north]
	 * @param layer input layer name
	 * @return extent
	 */
	public double[] getExtent(String layer) {
		if(wmsLayers.get(layer) == null) 
			return null;
		return wmsLayers.get(layer).getExtent();
	}
	
	/**
	 * WFS layer description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	public static class WMSLayer {
		
		private String name;
		private Set<String> supportedCRS = new HashSet<String>();
		private String bboxWest, bboxEast, bboxNorth, bboxSouth;
		private String style;
		
		private final String LAYER_NAME = ".*(?i)Name";
		private final String LAYER_CRS = ".*(?i)CRS";
		private final String LAYER_BBOX = ".*(?i)EX_GeographicBoundingBox";
		private final String LAYER_BBOX_WEST = ".*(?i)westBoundLongitude";
		private final String LAYER_BBOX_EAST = ".*(?i)eastBoundLongitude";
		private final String LAYER_BBOX_SOUTH = ".*(?i)southBoundLatitude";
		private final String LAYER_BBOX_NORTH = ".*(?i)northBoundLatitude";
		private final String LAYER_STYLE = ".*(?i)Style";
		private final String LAYER_STYLE_NAME = ".*(?i)Name";
		
		public WMSLayer(Node layerNode) {
			//get child nodes
			NodeList layerNodes = layerNode.getChildNodes();
			//iterate
			for (int i=0; i<layerNodes.getLength(); i++) {
				//get element node
				Node element = layerNodes.item(i);
				//check name
				if(element.getNodeName().matches(LAYER_NAME)){
					this.setName(element.getTextContent().trim());
				}
				//check crs
				if(element.getNodeName().matches(LAYER_CRS)) {
					this.addSupportedCRS(element.getTextContent().trim());
				}
				//check bbox
				if(element.getNodeName().matches(LAYER_BBOX)) {
					NodeList bboxElements = element.getChildNodes();
					for (int j=0; j<bboxElements.getLength(); j++) {
						Node bboxElement = bboxElements.item(j);
						if(bboxElement.getNodeName().matches(LAYER_BBOX_WEST))
							this.setBBoxWest(bboxElement.getTextContent().trim());
						else if(bboxElement.getNodeName().matches(LAYER_BBOX_EAST))
							this.setBBoxEast(bboxElement.getTextContent().trim());
						else if(bboxElement.getNodeName().matches(LAYER_BBOX_NORTH))
							this.setBBoxNorth(bboxElement.getTextContent().trim());
						else if(bboxElement.getNodeName().matches(LAYER_BBOX_SOUTH))
							this.setBBoxSouth(bboxElement.getTextContent().trim());
					}					
				}
				//check style
				if(element.getNodeName().matches(LAYER_STYLE)) {
					NodeList styleElements = element.getChildNodes();
					for (int j=0; j<styleElements.getLength(); j++) {
						Node styleElement = styleElements.item(j);
						if(styleElement.getNodeName().matches(LAYER_STYLE_NAME))
							this.setStyle(styleElement.getTextContent().trim());
					}
				}
			}
		}
		
		public String getName() { return name; }
		public void setName(String name){ this.name = name; }
		
		public Set<String> getSupportedCRS() { return supportedCRS; }
		public void addSupportedCRS(String supportedSRS){ this.supportedCRS.add(supportedSRS); }		

		public String getBBoxWest() { return bboxWest; }
		public void setBBoxWest(String bboxWest) { this.bboxWest = bboxWest; }

		public String getBBoxEast() { return bboxEast; }
		public void setBBoxEast(String bboxEast) { this.bboxEast = bboxEast; }
		
		public String getBBoxNorth() { return bboxNorth; }
		public void setBBoxNorth(String bboxNorth) { this.bboxNorth = bboxNorth; }
		
		public String getBBoxSouth() { return bboxSouth; }
		public void setBBoxSouth(String bboxSouth) { this.bboxSouth = bboxSouth; }
		
		public String getStyle() { return style; }
		public void setStyle(String style) { this.style = style; }
		
		/**
		 * get extent [west,south,east,north]
		 * @return extent
		 */
		public double[] getExtent() {
			if(bboxWest == null || bboxEast == null || bboxNorth == null || bboxSouth == null)
				return null;
			return new double[]{Double.parseDouble(bboxWest), Double.parseDouble(bboxEast), Double.parseDouble(bboxNorth), Double.parseDouble(bboxSouth)};
		}
		
		/**
		 * get center coordinate for layer [lat,lon]
		 * @return center coordinate
		 */
		public double[] getCenter() {
			double[] extent = this.getExtent();
			return extent == null ? null : new double[]{((extent[0] + extent[2]) / 2), ((extent[1] + extent[3]) / 2)};
		}
		
	}

}
