package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * standard WMS capabilities
 */
public class WMSCapabilities extends OWSCapabilities {

    private static final String WMS_LAYER = ".*(?i)Layer";
    private static final String SERVICE_WMS = "(?i)WMS";

    private Map<String, WMSLayer> wmsLayers;

    /**
     * Constructor
     *
     * @param uri      WMS capabilities uri
     * @param object   capabilities document
     * @param metadata document metadata
     */
    public WMSCapabilities(@NotNull URLLiteral uri, Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initWMSCapabilities();
    }

    /**
     * Constructor
     *
     * @param capabilities input capabilities
     */
    public WMSCapabilities(@NotNull OWSCapabilities capabilities) {
        super(capabilities.getURI(), capabilities.resolve(), capabilities.getMetadata(), capabilities.getServiceIdentification(), capabilities.getOperationsMetadata());
        initWMSCapabilities();
    }

    /**
     * initialize WFS capabilities
     */
    private void initWMSCapabilities() {
        if (!this.getServiceType().matches(SERVICE_WMS))
            throw new IllegalArgumentException("Document is not a valid WMS capabilities document");
        this.wmsLayers = new HashMap<>();
        List<Node> matches = this.getNodes(WMS_LAYER);
        for (Node node : matches) {
            WMSLayer layer = new WMSLayer(node);
            if (layer.getName() != null)
                wmsLayers.put(layer.getName(), layer);
        }
    }

    /**
     * get WFS layer names
     *
     * @return layer names
     */
    public Set<String> getWMSLayers() {
        return wmsLayers.keySet();
    }

    /**
     * get center for provided layer as string (x,y), required for OpenLayers
     *
     * @param layer input layer name
     * @return center or null, of layer does not exist
     */
    public String getCenter(String layer) {
        if (wmsLayers.get(layer) == null)
            return null;
        double[] center = wmsLayers.get(layer).getCenter();
        if (center == null)
            return null;
        return "[" + center[0] + "," + center[1] + "]";
    }

    /**
     * get extent of layer [west,south,east,north]
     *
     * @param layer input layer name
     * @return extent
     */
    public double[] getExtent(String layer) {
        if (wmsLayers.get(layer) == null)
            return null;
        return wmsLayers.get(layer).getExtent();
    }

    /**
     * WFS layer description
     *
     * @author Stefan Wiemann, TU Dresden
     */
    public static class WMSLayer {

        private final String LAYER_NAME = ".*(?i)Name";
        private final String LAYER_TITLE = ".*(?i)Title";
        private final String LAYER_CRS = ".*(?i)CRS";
        private final String LAYER_BBOX = ".*(?i)EX_GeographicBoundingBox";
        private final String LAYER_BBOX_WEST = ".*(?i)westBoundLongitude";
        private final String LAYER_BBOX_EAST = ".*(?i)eastBoundLongitude";
        private final String LAYER_BBOX_SOUTH = ".*(?i)southBoundLatitude";
        private final String LAYER_BBOX_NORTH = ".*(?i)northBoundLatitude";
        private final String LAYER_STYLE = ".*(?i)Style";
        private final String LAYER_STYLE_NAME = ".*(?i)Name";
        private String name, title;
        private Set<String> supportedCRS = new HashSet<>();
        private String bboxWest, bboxEast, bboxNorth, bboxSouth;
        private String style;

        public WMSLayer(Node layerNode) {
            //get child nodes
            NodeList layerNodes = layerNode.getChildNodes();
            //iterate
            for (int i = 0; i < layerNodes.getLength(); i++) {
                //get element node
                Node element = layerNodes.item(i);
                //check name
                if (element.getNodeName().matches(LAYER_NAME)) {
                    this.setName(element.getTextContent().trim());
                }
                //check title
                if (element.getNodeName().matches(LAYER_TITLE)) {
                    this.setTitle(element.getTextContent().trim());
                }
                //check crs
                if (element.getNodeName().matches(LAYER_CRS)) {
                    this.addSupportedCRS(element.getTextContent().trim());
                }
                //check bbox
                if (element.getNodeName().matches(LAYER_BBOX)) {
                    NodeList bboxElements = element.getChildNodes();
                    for (int j = 0; j < bboxElements.getLength(); j++) {
                        Node bboxElement = bboxElements.item(j);
                        if (bboxElement.getNodeName().matches(LAYER_BBOX_WEST))
                            this.setBBoxWest(bboxElement.getTextContent().trim());
                        else if (bboxElement.getNodeName().matches(LAYER_BBOX_EAST))
                            this.setBBoxEast(bboxElement.getTextContent().trim());
                        else if (bboxElement.getNodeName().matches(LAYER_BBOX_NORTH))
                            this.setBBoxNorth(bboxElement.getTextContent().trim());
                        else if (bboxElement.getNodeName().matches(LAYER_BBOX_SOUTH))
                            this.setBBoxSouth(bboxElement.getTextContent().trim());
                    }
                }
                //check style
                if (element.getNodeName().matches(LAYER_STYLE)) {
                    NodeList styleElements = element.getChildNodes();
                    for (int j = 0; j < styleElements.getLength(); j++) {
                        Node styleElement = styleElements.item(j);
                        if (styleElement.getNodeName().matches(LAYER_STYLE_NAME))
                            this.setStyle(styleElement.getTextContent().trim());
                    }
                }
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Set<String> getSupportedCRS() {
            return supportedCRS;
        }

        public void addSupportedCRS(String supportedSRS) {
            this.supportedCRS.add(supportedSRS);
        }

        public String getBBoxWest() {
            return bboxWest;
        }

        public void setBBoxWest(String bboxWest) {
            this.bboxWest = bboxWest;
        }

        public String getBBoxEast() {
            return bboxEast;
        }

        public void setBBoxEast(String bboxEast) {
            this.bboxEast = bboxEast;
        }

        public String getBBoxNorth() {
            return bboxNorth;
        }

        public void setBBoxNorth(String bboxNorth) {
            this.bboxNorth = bboxNorth;
        }

        public String getBBoxSouth() {
            return bboxSouth;
        }

        public void setBBoxSouth(String bboxSouth) {
            this.bboxSouth = bboxSouth;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        /**
         * get extent [west,south,east,north]
         *
         * @return extent
         */
        public double[] getExtent() {
            if (bboxWest == null || bboxEast == null || bboxNorth == null || bboxSouth == null)
                return null;
            return new double[]{Double.parseDouble(bboxWest), Double.parseDouble(bboxEast), Double.parseDouble(bboxNorth), Double.parseDouble(bboxSouth)};
        }

        /**
         * get center coordinate for layer [lat,lon]
         *
         * @return center coordinate
         */
        public double[] getCenter() {
            double[] extent = this.getExtent();
            return extent == null ? null : new double[]{((extent[0] + extent[2]) / 2), ((extent[1] + extent[3]) / 2)};
        }

    }

}