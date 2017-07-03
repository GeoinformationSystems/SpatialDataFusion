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
 * standard WFS capabilities
 */
public class WFSCapabilities extends OWSCapabilities {

    private static final String WFS_LAYER = ".*(?i)FeatureType";
    private static final String SERVICE_WFS = "(?i)WFS";

    private Map<String, WFSLayer> wfsLayers;
    private WFSOutputDescription outputDescription;

    /**
     * Constructor
     *
     * @param uri      WFS capabilities uri
     * @param object   capabilities document
     * @param metadata document metadata
     */
    public WFSCapabilities(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initWFSCapabilities();
    }

    /**
     * Constructor
     *
     * @param capabilities input capabilities
     */
    public WFSCapabilities(@NotNull OWSCapabilities capabilities) {
        super(capabilities.getURI(), capabilities.resolve(), capabilities.getMetadata(), capabilities.getServiceIdentification(), capabilities.getOperationsMetadata());
        initWFSCapabilities();
    }

    /**
     * initialize WFS capabilities
     */
    private void initWFSCapabilities() {
        if (!this.getServiceType().matches(SERVICE_WFS))
            throw new IllegalArgumentException("Document is not a valid WFS capabilities document");
        this.wfsLayers = new HashMap<>();
        List<Node> matches = this.getNodes(WFS_LAYER);
        for (Node node : matches) {
            WFSLayer layer = new WFSLayer(node);
            if (layer.getName() != null)
                wfsLayers.put(layer.getName(), layer);
        }
        initOutputDescription();
    }

    /**
     * initialize output description
     */
    private void initOutputDescription() {
        this.outputDescription = new WFSOutputDescription(this.getOperationsMetadata());
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
     * get WFS layer by name
     *
     * @return layer name
     */
    @Nullable
    public WFSLayer getWFSLayer(String name) {
        return wfsLayers.get(name);
    }

    /**
     * get center for provided layer as string (x,y), required for OpenLayers
     *
     * @param layer input layer name
     * @return center or null, of layer does not exist
     */
    public @Nullable String getCenter(@NotNull String layer) {
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
    public @Nullable double[] getExtent(@NotNull String layer) {
        if (wfsLayers.get(layer) == null)
            return null;
        return wfsLayers.get(layer).getExtent();
    }

    /**
     * get WFS output description
     *
     * @return WFS output description
     */
    public @NotNull WFSOutputDescription getOutputDescription() {
        return outputDescription;
    }

    /**
     * WFS layer description
     */
    public class WFSLayer {

        private final String LAYER_NAME = ".*(?i)Name";
        private final String LAYER_CRS = ".*(?i)defaultCRS|defaultSRS|supportedCRS|supportedSRS";
        private final String LAYER_BBOX = ".*(?i)WGS84BoundingBox";
        private final String LAYER_BBOX_LC = ".*(?i)LowerCorner";
        private final String LAYER_BBOX_UC = ".*(?i)UpperCorner";
        private String name;
        private Set<String> supportedSRS = new HashSet<>();
        private String lowerCorner;
        private String upperCorner;

        WFSLayer(@NotNull Node layerNode) {
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

        void setName(String name) {
            this.name = name;
        }

        public Set<String> getSupportedSRS() {
            return supportedSRS;
        }

        void addSupportedSRS(String supportedSRS) {
            this.supportedSRS.add(supportedSRS);
        }

        public String getLowerCorner() {
            return lowerCorner;
        }

        void setLowerCorder(String lc) {
            this.lowerCorner = lc;
        }

        public String getUpperCorner() {
            return upperCorner;
        }

        void setUpperCorder(String uc) {
            this.upperCorner = uc;
        }

        /**
         * get extent [west,south,east,north]
         *
         * @return extent
         */
        @Nullable double[] getExtent() {
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
        @Nullable double[] getCenter() {
            double[] extent = this.getExtent();
            if (extent == null)
                return null;
            return new double[]{((extent[0] + extent[2]) / 2), ((extent[1] + extent[3]) / 2)};
        }

    }

    /**
     * WFS layer description
     */
    public class WFSOutputDescription {

        private final String OPERATION_GETFEATURE = "GetFeature";
        private final String GETFEATURE_IOFORMATS = "outputFormat";
        private final String DEFAULT_FORMAT = ".*(?i)gml/3.*";

        private String identifier, title, description;
        private IOFormat defaultFormat;
        private Set<IOFormat> supportedFormats = new HashSet<>();

        WFSOutputDescription(OperationsMetadata operationsMetadata) {
            this.supportedFormats = new HashSet<>();
            Set<String> formats = operationsMetadata.getOperationMetadata(OPERATION_GETFEATURE).getParameterValues(GETFEATURE_IOFORMATS);
            for (String format : formats) {
                this.supportedFormats.add(new IOFormat(format, null, null));
            }
            this.defaultFormat = initDefaultFormat();
        }

        private IOFormat initDefaultFormat() {
            for (IOFormat format : this.supportedFormats) {
                if (format.getMimetype() != null && format.getMimetype().matches(DEFAULT_FORMAT))
                    return format;
            }
            return this.supportedFormats.iterator().next();
        }

        /**
         * get default format
         *
         * @return default format
         */
        public @NotNull IOFormat getDefaultFormat() {
            return this.defaultFormat;
        }

        /**
         * get supported formats
         *
         * @return supported formats
         */
        public @NotNull Set<IOFormat> getSupportedFormats() {
            return supportedFormats;
        }

    }

}
