package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * standard WFS capabilities
 */
public class WFSCapabilities extends OWSCapabilities {

    private static final String WFS_LAYER = ".*(?i)FeatureType";

    private Map<String, WFSLayer> wfsLayers;

    /**
     * Constructor
     *
     * @param uri      WFS capabilities uri
     * @param object   capabilities document
     * @param metadata document metadata
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public WFSCapabilities(@NotNull URILiteral uri, @NotNull Document object, @Nullable IMetadata metadata) throws ParserConfigurationException, SAXException, IOException {
        super(uri, object, metadata);
        if (!this.getServiceType().equals(OWSServiceType.WFS))
            throw new IOException("Document is not a WFS capabilities document");
        initWFSCapabilities();
    }

    /**
     * Constructor
     *
     * @param capabilities input capabilities
     */
    public WFSCapabilities(@NotNull OWSCapabilities capabilities) throws ParserConfigurationException, SAXException, IOException {
        this(capabilities.getURI(), capabilities.resolve(), capabilities.getMetadata());
    }

    /**
     * initialize WFS capabilities
     */
    private void initWFSCapabilities() {
        this.wfsLayers = new HashMap<>();
        List<Node> matches = this.getNodes(WFS_LAYER);
        for (Node node : matches) {
            WFSLayer layer = new WFSLayer(node);
            if (layer.getName() != null)
                wfsLayers.put(layer.getName(), layer);
        }
    }

    /**
     * get WFS layer names
     *
     * @return layer names
     */
    @NotNull
    public Set<String> getWFSLayers() {
        return wfsLayers.keySet();
    }

    /**
     * get center for provided layer as string (x,y), required for OpenLayers
     *
     * @param layer input layer name
     * @return center or null, of layer does not exist
     */
    public String getCenter(@NotNull String layer) {
        if (wfsLayers.get(layer) == null)
            return null;
        double[] center = wfsLayers.get(layer).getCenter();
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
    public double[] getExtent(@NotNull String layer) {
        if (wfsLayers.get(layer) == null)
            return null;
        return wfsLayers.get(layer).getExtent();
    }

    /**
     * WFS layer description
     *
     * @author Stefan Wiemann, TU Dresden
     */
    public static class WFSLayer {

        private final String LAYER_NAME = ".*(?i)Name";
        private final String LAYER_CRS = ".*(?i)defaultCRS|defaultSRS|supportedCRS|supportedSRS";
        private final String LAYER_BBOX = ".*(?i)WGS84BoundingBox";
        private final String LAYER_BBOX_LC = ".*(?i)LowerCorner";
        private final String LAYER_BBOX_UC = ".*(?i)UpperCorner";
        private String name;
        private Set<String> supportedSRS = new HashSet<String>();
        private String lowerCorner;
        private String upperCorner;

        public WFSLayer(@NotNull Node layerNode) {
            //get child nodes
            NodeList layerNodes = layerNode.getChildNodes();
            //iterate
            for (int i = 0; i < layerNodes.getLength(); i++) {
                //get element node
                Node element = layerNodes.item(i);
                //check for name
                if (element.getNodeName().matches(LAYER_NAME)) {
                    this.setName(element.getTextContent().trim());
                }
                //check for crs srs
                if (element.getNodeName().matches(LAYER_CRS)) {
                    this.addSupportedSRS(element.getTextContent().trim());
                }
                //check for bbox
                if (element.getNodeName().matches(LAYER_BBOX)) {
                    NodeList bboxElements = element.getChildNodes();
                    for (int j = 0; j < bboxElements.getLength(); j++) {
                        Node bboxElement = bboxElements.item(j);
                        if (bboxElement.getNodeName().matches(LAYER_BBOX_LC))
                            this.setLowerCorder(bboxElement.getTextContent().trim());
                        else if (bboxElement.getNodeName().matches(LAYER_BBOX_UC))
                            this.setUpperCorder(bboxElement.getTextContent().trim());
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

        public Set<String> getSupportedSRS() {
            return supportedSRS;
        }

        public void addSupportedSRS(String supportedSRS) {
            this.supportedSRS.add(supportedSRS);
        }

        public String getLowerCorner() {
            return lowerCorner;
        }

        public void setLowerCorder(String lc) {
            this.lowerCorner = lc;
        }

        public String getUpperCorner() {
            return upperCorner;
        }

        public void setUpperCorder(String uc) {
            this.upperCorner = uc;
        }

        /**
         * get extent [west,south,east,north]
         *
         * @return extent
         */
        public double[] getExtent() {
            if (lowerCorner == null || upperCorner == null)
                return null;
            String[] aLc = this.getLowerCorner().split(" ");
            String[] aUc = this.getUpperCorner().split(" ");
            return new double[]{Double.parseDouble(aLc[0]), Double.parseDouble(aLc[1]), Double.parseDouble(aUc[0]), Double.parseDouble(aUc[1])};
        }

        /**
         * get center coordinate for layer
         *
         * @return center coordinate
         */
        public double[] getCenter() {
            double[] extent = this.getExtent();
            if (extent == null)
                return null;
            return new double[]{((extent[0] + extent[2]) / 2), ((extent[1] + extent[3]) / 2)};
        }

    }

}
