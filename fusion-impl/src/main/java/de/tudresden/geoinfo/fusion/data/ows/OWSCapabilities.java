package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * standard OWS capabilities
 */
public class OWSCapabilities extends XMLResponse {

    public static String NODE_SERVICE_IDENTIFICATION = ".*(?i)ServiceIdentification|Service";
    public static String NODE_OPERATION_METADATA = ".*(?i)OperationsMetadata|Capability";

    private ServiceIdentification serviceIdentification;
    private OperationsMetadata operationsMetadata;


    /**
     * constructor
     *
     * @param uri      OWS base uri literal
     * @param object   OWS capabilities document
     * @param metadata OWS capabilities metadata
     */
    public OWSCapabilities(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initCapabilities();
    }

    /**
     * constructor
     *
     * @param serviceIdentification service identification
     * @param operationsMetadata    operation metadtaa
     */
    OWSCapabilities(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata, ServiceIdentification serviceIdentification, OperationsMetadata operationsMetadata) {
        super(uri, object, metadata);
        this.serviceIdentification = serviceIdentification;
        this.operationsMetadata = operationsMetadata;
    }

    /**
     * parse capabilities
     */
    private void initCapabilities() {
        initServiceIdentification();
        if (!this.getServiceType().equalsIgnoreCase("WMS"))
            initOperations();
    }

    /**
     * parse service identification
     */
    private void initServiceIdentification() {
        Node serviceIdentificationNode = getNode(NODE_SERVICE_IDENTIFICATION);
        if (serviceIdentificationNode == null)
            throw new IllegalArgumentException("Document does not provide OWS service identification");
        this.serviceIdentification = new ServiceIdentification(serviceIdentificationNode);
    }

    /**
     * parse operations metadata
     */
    private void initOperations() {
        Node operationsMetadataNode = getNode(NODE_OPERATION_METADATA);
        if (operationsMetadataNode == null)
            throw new IllegalArgumentException("Document does not provide OWS operations metadata");
        this.operationsMetadata = new OperationsMetadata(operationsMetadataNode);
    }

    /**
     * get service identification metadata
     *
     * @return service identification metadata
     */
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * get service operations metadata
     *
     * @return service operations metadata
     */
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * get OWS service type
     *
     * @return OWS service type
     */
    public @NotNull String getServiceType() {
        return this.getServiceIdentification().getServiceType();
    }

    /**
     * nested class: service identification
     */
    public class ServiceIdentification {

        private final static String NODE_SERVICE_TYPE = ".*(?i)ServiceType|Name";
        private final static String NODE_SERVICE_TYPE_VERSION = ".*(?i)ServiceTypeVersion";

        private Set<String> serviceTypeVersions;
        private String serviceType;

        /**
         * constructor
         *
         * @param serviceIdentificationNode service identification node
         */
        ServiceIdentification(@NotNull Node serviceIdentificationNode) {
            NodeList identificationNodes = serviceIdentificationNode.getChildNodes();
            for (int i = 0; i < identificationNodes.getLength(); i++) {
                Node element = identificationNodes.item(i);
                //check for service type
                if (element.getNodeName().matches(NODE_SERVICE_TYPE)) {
                    this.serviceType = element.getTextContent().trim();
                }
                //check for service type version
                if (element.getNodeName().matches(NODE_SERVICE_TYPE_VERSION)) {
                    if (this.serviceTypeVersions == null)
                        this.serviceTypeVersions = new HashSet<>();
                    this.serviceTypeVersions.add(element.getTextContent().trim());
                }
            }
            validate();
        }

        /**
         * validate service identification
         */
        private void validate() {
            if (this.serviceType == null)
                throw new IllegalArgumentException("Document does not provide OWS service type");
        }

        /**
         * get service type
         *
         * @return service type URN
         */
        public @NotNull String getServiceType() {
            return this.serviceType;
        }

        /**
         * get supported service type versions
         *
         * @return service type version
         */
        public @NotNull Set<String> getSupportedVersions() {
            return this.serviceTypeVersions;
        }

    }

    /**
     * nested class: operations metadata
     */
    public class OperationsMetadata {

        private static final String NODE_OPERATION = ".*(?i)Operation";

        private Map<String, OperationMetadata> operationsMetadata;

        /**
         * constructor
         *
         * @param operationsMetadataNode operations metadata node
         */
        OperationsMetadata(@NotNull Node operationsMetadataNode) {
            NodeList operations = operationsMetadataNode.getChildNodes();
            for (int i = 0; i < operations.getLength(); i++) {
                Node element = operations.item(i);
                if (element.getNodeName().matches(NODE_OPERATION)) {
                    OperationMetadata operationMetadata = new OperationMetadata(element);
                    if (this.operationsMetadata == null)
                        this.operationsMetadata = new HashMap<>();
                    this.operationsMetadata.put(operationMetadata.getName().toLowerCase(), new OperationMetadata(element));
                }
            }
            validate();
        }

        /**
         * validate operations metadata
         */
        private void validate() {
            if (this.operationsMetadata == null)
                throw new IllegalArgumentException("Document does not provide OWS service operations");
        }

        /**
         * get operation metadata
         *
         * @param name operation name
         * @return associated operation metadata
         */
        public @Nullable OperationMetadata getOperationMetadata(@NotNull String name) {
            return this.operationsMetadata.get(name.toLowerCase());
        }

        /**
         * nested class: operation metadata
         */
        public class OperationMetadata {

            private static final String NAME = "(?i)name";
            private static final String OPERATION_DCP = ".*(?i)DCP";
            private static final String OPERATION_DCP_GET = ".*(?i)Get";
            private static final String OPERATION_DCP_GET_LINK = ".*(?i)href";
            private static final String OPERATION_PARAMETER = ".*(?i)Parameter";
            private static final String OPERATION_PARAMETER_VALUE = ".*(?i)Value";

            private String name;
            private String httpGetBase;
            private Map<String, Set<String>> parameters = new HashMap<>();

            /**
             * constructor
             *
             * @param operationMetadataNode operation metadata node
             */
            OperationMetadata(@NotNull Node operationMetadataNode) {
                this.name = getAttributeValue(NAME, operationMetadataNode.getAttributes());
                NodeList metadataNodes = operationMetadataNode.getChildNodes();
                for (int i = 0; i < metadataNodes.getLength(); i++) {
                    Node metadataNode = metadataNodes.item(i);
                    //set http get URI
                    if (metadataNode.getNodeName().matches(OPERATION_DCP)) {
                        Node httpGetNode = getNode(OPERATION_DCP_GET, metadataNode.getChildNodes());
                        if (httpGetNode != null)
                            this.httpGetBase = getAttributeValue(OPERATION_DCP_GET_LINK, httpGetNode.getAttributes());
                    }
                    //set parameter name and values
                    else if (metadataNode.getNodeName().matches(OPERATION_PARAMETER)) {
                        String name = getAttributeValue(NAME, metadataNode.getAttributes());
                        if (name == null)
                            continue;
                        Set<String> values = new HashSet<>();
                        NodeList parameterNodes = metadataNode.getChildNodes();
                        for (int j = 0; j < parameterNodes.getLength(); j++) {
                            Node parameterNode = parameterNodes.item(j);
                            if (parameterNode.getNodeName().matches(OPERATION_PARAMETER_VALUE))
                                values.add(parameterNode.getTextContent().trim());
                        }
                        this.parameters.put(name.toLowerCase(), values);
                    }
                }
                validate();
            }

            /**
             * validate operation metadata
             */
            private void validate() {
                if (this.name == null)
                    throw new IllegalArgumentException("OWS service operation does not specify a name");
                if (this.httpGetBase == null)
                    throw new IllegalArgumentException("OWS service operation does not specify a DCP HTTP GET access point");
            }

            /**
             * get operation name
             *
             * @return operation name
             */
            public @NotNull String getName() {
                return this.name;
            }

            /**
             * get operation DCP HTTP GET URL
             *
             * @return operation GET access URL
             */
            public @NotNull String getHTTPGETBase() {
                return this.httpGetBase;
            }

            /**
             * get operation parameters
             *
             * @return operation parameters
             */
            public @NotNull Set<String> getParameters() {
                return this.parameters.keySet();
            }

            /**
             * get operation parameter values
             *
             * @return operation parameter values
             */
            public @NotNull Set<String> getParameterValues(String parameter) {
                return this.parameters.get(parameter.toLowerCase());
            }

        }

    }

}
