package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * standard WPS describe process document
 */
public class WPSDescribeProcess extends XMLResponse {

    private static final String PROCESS_DESCRIPTION = ".*(?i)ProcessDescription$";

    private Map<String, WPSProcessDescription> wpsProcesses;

    /**
     * Constructor
     *
     * @param uri    WPS description uri
     * @param object WPS description document
     */
    public WPSDescribeProcess(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initWPSProcessDescription();
    }

    /**
     * parse WPS process description
     */
    private void initWPSProcessDescription() {
        List<Node> matches = this.getNodes(PROCESS_DESCRIPTION);
        wpsProcesses = new HashMap<>();
        for (Node description : matches) {
            WPSProcessDescription process = new WPSProcessDescription(this.getURI(), description);
            wpsProcesses.put(process.getIdentifier(), process);
        }
    }

    /**
     * get WPS process identifier
     *
     * @return process description identifier
     */
    @NotNull
    public Set<String> getProcessIdentifier() {
        return wpsProcesses.keySet();
    }

    /**
     * get WPS process description by identifier
     *
     * @param identifier description identifier
     * @return WPS process description with specified identifier
     */
    @NotNull
    public WPSProcessDescription getProcessDescription(@NotNull String identifier) {
        return this.wpsProcesses.get(identifier);
    }

    /**
     * nested class WPS process description
     */
    public class WPSProcessDescription {

        private static final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
        private static final String PROCESS_TITLE = ".*(?i)Title";
        private static final String PROCESS_ABSTRACT = ".*(?i)Abstract";
        private static final String PROCESS_INPUTS = ".*(?i)DataInputs";
        private static final String PROCESS_OUTPUTS = ".*(?i)ProcessOutputs";
        private static final String PROCESS_INPUT = ".*(?i)Input";
        private static final String PROCESS_OUTPUT = ".*(?i)Output";

        private String identifier;
        private String title;
        private String description;
        private URLLiteral uri;
        private Map<String, WPSIODescription> inputs = new HashMap<>();
        private Map<String, WPSIODescription> outputs = new HashMap<>();

        public WPSProcessDescription(URLLiteral uri, Node ioNode) {
            this.uri = uri;
            //get child nodes
            NodeList layerNodes = ioNode.getChildNodes();
            //iterate
            for (int i = 0; i < layerNodes.getLength(); i++) {
                //get element node
                Node element = layerNodes.item(i);
                //check for identifier
                if (element.getNodeName().matches(PROCESS_IDENTIFIER)) {
                    this.setIdentifier(element.getTextContent().trim());
                }
                //check for title
                else if (element.getNodeName().matches(PROCESS_TITLE)) {
                    this.setTitle(element.getTextContent().trim());
                }
                //check for description
                else if (element.getNodeName().matches(PROCESS_ABSTRACT)) {
                    this.setDescription(element.getTextContent().trim());
                }
                //check for inputs
                else if (element.getNodeName().matches(PROCESS_INPUTS)) {
                    NodeList inputs = element.getChildNodes();
                    for (int j = 0; j < inputs.getLength(); j++) {
                        Node inputElement = inputs.item(j);
                        if (inputElement.getNodeName().matches(PROCESS_INPUT)) {
                            this.addInput(inputElement);
                        }
                    }
                }
                //check for outputs
                else if (element.getNodeName().matches(PROCESS_OUTPUTS)) {
                    NodeList inputs = element.getChildNodes();
                    for (int j = 0; j < inputs.getLength(); j++) {
                        Node outputElement = inputs.item(j);
                        if (outputElement.getNodeName().matches(PROCESS_OUTPUT)) {
                            this.addOutput(outputElement);
                        }
                    }
                }
            }
        }

        private void addInput(Node element) {
            WPSIODescription ioDesc = new WPSIODescription(element);
            inputs.put(ioDesc.getIdentifier(), ioDesc);
        }

        private void addOutput(Node element) {
            WPSIODescription ioDesc = new WPSIODescription(element);
            outputs.put(ioDesc.getIdentifier(), ioDesc);
        }

        public Map<String, WPSIODescription> getInputs() {
            return inputs;
        }

        public Map<String, WPSIODescription> getOutputs() {
            return outputs;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * get base URI of the response
         *
         * @return base URI literal
         */
        public URLLiteral getURI() {
            return this.uri;
        }

        /**
         * nested class IO Metadata for WPS Process
         */
        public class WPSIODescription {

            private static final String IO_IDENTIFIER = ".*(?i)Identifier";
            private static final String IO_TITLE = ".*(?i)Title";
            private static final String IO_ABSTRACT = ".*(?i)Abstract";
            private static final String IO_FORMAT = ".*(?i)Format";
            private static final String IO_MIMETYPE = ".*(?i)MimeType";
            private static final String IO_SCHEMA = ".*(?i)Schema";
            private static final String IO_LITERAL = ".*(?i)LiteralData|.*(?i)LiteralOutput";
            private static final String IO_LITERAL_TYPE = ".*(?i)DataType";
            private static final String IO_LITERAL_REF = ".*(?i)reference";
            private static final String IO_BBOX = ".*(?i)BoundingBoxData|.*(?i)BoundingBoxOutput";
            private static final String IO_BBOX_DEFAULT = ".*(?i)Default";
            private static final String IO_BBOX_SUPPORTED = ".*(?i)Supported";
            private static final String IO_BBOX_CRS = ".*(?i)CRS";
            private static final String PROCESS_INPUT_MIN_OCCURS = ".*(?i)minOccurs";
            private static final String PROCESS_INPUT_MAX_OCCURS = ".*(?i)maxOccurs";

            private String identifier, title, description;
            private Set<IOFormat> supportedFormats = new TreeSet<>();
            private IOFormat defaultFormat;
            private int minOccurs, maxOccurs;

            public WPSIODescription(Node ioNode) {
                //get min occurs
                String min = getAttributeValue(PROCESS_INPUT_MIN_OCCURS, ioNode.getAttributes());
                this.minOccurs = min != null ? Integer.parseInt(min) : 1;
                //get max occurs
                String max = getAttributeValue(PROCESS_INPUT_MAX_OCCURS, ioNode.getAttributes());
                this.maxOccurs = max != null ? Integer.parseInt(max) : 1;
                //get child nodes
                NodeList ioNodes = ioNode.getChildNodes();
                //iterate
                for (int i = 0; i < ioNodes.getLength(); i++) {
                    //get element node
                    Node element = ioNodes.item(i);
                    //check for identifier
                    if (element.getNodeName().matches(IO_IDENTIFIER)) {
                        this.identifier = element.getTextContent().trim();
                    }
                    //check for title
                    if (element.getNodeName().matches(IO_TITLE)) {
                        this.title = element.getTextContent().trim();
                    }
                    //check for abstract
                    if (element.getNodeName().matches(IO_ABSTRACT)) {
                        this.description = element.getTextContent().trim();
                    }
                }

                //select all complex format elements
                List<Node> formatNodes = new ArrayList<>();
                XMLResponse.getNodes(IO_FORMAT, ioNode.getChildNodes(), formatNodes);
                //set formats
                for (Node formatNode : formatNodes) {
                    NodeList nodes = formatNode.getChildNodes();
                    IOFormat format = new IOFormat(null, null, null);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        //get element node
                        Node element = nodes.item(i);
                        //check for mimetype
                        if (element.getNodeName().matches(IO_MIMETYPE)) {
                            format.setMimetype(element.getTextContent().trim());
                        }
                        //check for schema
                        if (element.getNodeName().matches(IO_SCHEMA)) {
                            format.setSchema(element.getTextContent().trim());
                        }
                    }
                    //adds supported format; first format is set default
                    this.addSupportedFormat(format);
                    if (this.defaultFormat == null)
                        this.setDefaultFormat(format);
                }

                //select all literal data elements
                formatNodes.clear();
                XMLResponse.getNodes(IO_LITERAL, ioNode.getChildNodes(), formatNodes);
                //set formats
                for (Node formatNode : formatNodes) {
                    NodeList nodes = formatNode.getChildNodes();
                    IOFormat format = new IOFormat(null, null, null);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        //get element node
                        Node element = nodes.item(i);
                        //check for data type
                        if (element.getNodeName().matches(IO_LITERAL_TYPE)) {
                            String ref = getAttributeValue(IO_LITERAL_REF, element.getAttributes());
                            if (ref != null)
                                format.setType(ref);
                        }
                    }
                    //adds supported format; first format is set default
                    this.addSupportedFormat(format);
                    if (this.defaultFormat == null)
                        this.setDefaultFormat(format);
                }

                //select all bbox data elements
                formatNodes.clear();
                XMLResponse.getNodes(IO_BBOX, ioNode.getChildNodes(), formatNodes);
                //set formats
                for (Node formatNode : formatNodes) {
                    NodeList nodes = formatNode.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        //get element node
                        Node element = nodes.item(i);
                        //check for data type
                        if (element.getNodeName().matches(IO_BBOX_DEFAULT) || element.getNodeName().matches(IO_BBOX_SUPPORTED)) {
                            NodeList bboxNodes = element.getChildNodes();
                            IOFormat format = new IOFormat(null, null, null);
                            for (int j = 0; j < bboxNodes.getLength(); j++) {
                                Node bboxElement = bboxNodes.item(j);
                                if (bboxElement.getNodeName().matches(IO_BBOX_CRS)) {
                                    format.setType(bboxElement.getTextContent().trim());
                                }
                            }
                            //adds supported format; first format is set default
                            this.addSupportedFormat(format);
                            if (this.defaultFormat == null)
                                this.setDefaultFormat(format);
                        }
                    }
                }
            }

            public int getMinOccurs() {
                return this.minOccurs;
            }

            public int getMaxOccurs() {
                return this.maxOccurs;
            }

            public String getIdentifier() {
                return identifier;
            }

            public String getTitle() {
                return title;
            }

            public String getDescription() {
                return description;
            }

            public @NotNull Set<IOFormat> getSupportedFormats() {
                return supportedFormats;
            }

            private boolean addSupportedFormat(IOFormat supportedFormat) {
                return this.supportedFormats.add(supportedFormat);
            }

            public @NotNull IOFormat getDefaultFormat() {
                return defaultFormat;
            }

            private void setDefaultFormat(IOFormat defaultFormat) {
                this.defaultFormat = defaultFormat;
            }

            /**
             * get io connection constraints
             *
             * @return connection constraints
             */
            public final @NotNull Set<IConnectionConstraint> getConnectionConstraints() {
                Set<IConnectionConstraint> connectionConstraints = new HashSet<>();
                connectionConstraints.add(new IOFormatConstraint(this.getSupportedFormats()));
                return connectionConstraints;
            }

            /**
             * get io runtime constraints
             *
             * @return runtime constraints
             */
            public final @NotNull Set<IRuntimeConstraint> getRuntimeConstraints() {
                Set<IRuntimeConstraint> runtimeConstraints = new HashSet<>();
                if (this.getMinOccurs() > 0)
                    runtimeConstraints.add(new MandatoryDataConstraint());
                return runtimeConstraints;
            }

        }

    }

}
