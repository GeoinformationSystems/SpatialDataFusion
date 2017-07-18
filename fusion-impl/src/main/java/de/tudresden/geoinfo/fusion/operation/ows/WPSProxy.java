package de.tudresden.geoinfo.fusion.operation.ows;

import de.tud.fusion.XMLBuilder;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.literal.*;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess.WPSProcessDescription;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess.WPSProcessDescription.WPSIODescription;
import de.tudresden.geoinfo.fusion.data.ows.XMLResponse;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.WPSDescriptionParser;
import de.tudresden.geoinfo.fusion.operation.workflow.InputConnector;
import de.tudresden.geoinfo.fusion.operation.workflow.OutputConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Proxy WPS operation instance
 */
public class WPSProxy extends OWSServiceOperation {

    private static final String PROCESS_ID = WPSProxy.class.getName();
    private static final String PROCESS_DESCRIPTION = "Proxy for OGC WPS";

    private final static String PARSER_IN_RESOURCE = "IN_RESOURCE";
    private final static String PARSER_OUT_DESCRIPTION = "OUT_DESCRIPTION";

    private static final String PROCESS_OUTPUT = ".*(?i)Output$";
    private static final String IO_IDENTIFIER = ".*(?i)Identifier$";
    private static final String IO_REFERENCE = ".*(?i)Reference$";
    private static final String IO_REFERENCE_ATT = ".*(?i)href$";
    private static final String IO_REFERENCE_SCHEMA = ".*(?i)schema$";
    private static final String IO_REFERENCE_TYPE = ".*(?i)mimeType$";

    private final static String PARAM_SERVICE = "service";
    private final static String PARAM_REQUEST = "request";
    private final static String PARAM_IDENTIFIER = "identifier";
    private final static String VALUE_SERVICE = "WPS";
    private final static String VALUE_DESCRIBEPROCESS = "describeProcess";
    private final static String PARAM_VERSION = "version";
    private final static String VALUE_DEFAULT_VERSION = "1.0.0";

    private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Collections.singletonList("1.0.0"));

    private final static String REGEX_IGNORED_OUTPUT = "(?i)(OUT_RUNTIME)|(OUT_START)";

    private WPSProcessDescription processDescription;
    private Set<IInputConnector> hiddenConnectors = new HashSet<>();
    private String processId;
    private Map<String,IIdentifier> uniqueInputIdentifiers = new HashMap<>();
    private Map<String,IIdentifier> uniqueOutputIdentifiers = new HashMap<>();

    /**
     * constructor
     *
     * @param base       WPS base url
     */
    public WPSProxy(@NotNull URLLiteral base) {
        super(PROCESS_ID, PROCESS_DESCRIPTION, base);
    }

    @Override
    public void initializeConnectors() {
        if(this.getProcessDescription() != null) {
            super.initializeConnectors();
            this.setCapabilities();
        }
    }

    /**
     * get selected process identifier
     *
     * @return process identifier
     */
    public String getProcessId() {
        return this.processId;
    }

    /**
     * initialize WPS proxy for specified process
     *
     * @param processId process identifier
     */
    public void setProcessId(@NotNull String processId) {
        this.processId = processId;
        initProcessDescription(processId);
        super.initializeConnectors();
    }

    @Override
    public @Nullable String getSelectedOffering() {
        return this.getProcessId();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        this.setProcessId(offering);
    }

    /**
     * initialize process description
     *
     * @return process description
     */
    private void initProcessDescription(@NotNull String processId) {
        WPSDescriptionParser parser = new WPSDescriptionParser();
        parser.setInput(parser.getInputIdentifier(PARSER_IN_RESOURCE), getDescribeProcessRequest());
        parser.execute(null);

        IData processDescriptions = parser.getOutputData(PARSER_OUT_DESCRIPTION);
        if (!(processDescriptions instanceof WPSDescribeProcess))
            throw new IllegalArgumentException("Could not parse WPS process description for " + processId);

        if (!((WPSDescribeProcess) processDescriptions).getProcessIdentifier().contains(processId))
            throw new IllegalArgumentException("WPS does not provide process with identifier " + processId);

        this.processDescription = ((WPSDescribeProcess) processDescriptions).getProcessDescription(processId);
    }

    /**
     * get process description request
     *
     * @return process description request
     */
    private URLLiteral getDescribeProcessRequest() {
        this.setRequest(VALUE_DESCRIBEPROCESS);
        this.setParameter(PARAM_VERSION, VALUE_DEFAULT_VERSION);
        this.setParameter(PARAM_IDENTIFIER, this.getProcessId());
        return this.getRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST, PARAM_VERSION, PARAM_IDENTIFIER}, new String[]{});
    }

    private void setUniqueInputIdentifier(String identifier){
        this.uniqueInputIdentifiers.put(identifier, new ResourceIdentifier(null, identifier));
    }

    private IIdentifier getUniqueInputIdentifier(String identifier){
        return this.uniqueInputIdentifiers.get(identifier);
    }

    private String getInputKeyForIdentifier(IIdentifier uid){
        for(Map.Entry entry : this.uniqueInputIdentifiers.entrySet()){
            if(entry.getValue().equals(uid))
                return entry.getKey().toString();
        }
        //should not happen
        throw new IllegalArgumentException("UID " + uid + " is not associated with a key");
    }

    private void setUniqueOutputIdentifier(String identifier){
        this.uniqueOutputIdentifiers.put(identifier, new ResourceIdentifier(null, identifier));
    }

    private IIdentifier getUniqueOutputIdentifier(String identifier){
        return this.uniqueOutputIdentifiers.get(identifier);
    }

    private String getOutputKeyForIdentifier(IIdentifier uid){
        for(Map.Entry entry : this.uniqueOutputIdentifiers.entrySet()){
            if(entry.getValue().equals(uid))
                return entry.getKey().toString();
        }
        //should not happen
        throw new IllegalArgumentException("UID " + uid + " is not associated with a key");
    }

    /**
     * get underlying WPS process description
     *
     * @return WPS process description
     */
    public WPSProcessDescription getProcessDescription() {
        return this.processDescription;
    }

    @Override
    public String getService() {
        return VALUE_SERVICE;
    }

    @Override
    public @NotNull String getDefaultVersion() {
        return VALUE_DEFAULT_VERSION;
    }

    @Override
    public @NotNull Set<String> getSupportedVersions() {
        return SUPPORTED_VERSIONS;
    }

    @Override
    public @NotNull WPSCapabilities getCapabilities() {
        return (WPSCapabilities) super.getCapabilities();
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWPSProcesses();
    }

    @Override
    public void executeOperation() {
        String request = getXMLRequest();
        request = request.replace("&", "&amp;");
        System.out.println(request);
        try {
            XMLResponse response = executeRequest(request);
            setOutput(response);
        } catch (IOException | SAXException | ParserConfigurationException | URISyntaxException e) {
            throw new RuntimeException("Error while executing operation " + this.getProcessId(), e);
        }
    }

    private XMLResponse executeRequest(String request) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        //init connection
        URL url = this.getProcessDescription().getURI().getBase();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Length", String.valueOf(request.getBytes().length));
        //send request
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(request);
        out.flush();
        out.close();
        //get response
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(connection.getInputStream());
        return new XMLResponse(new URLLiteral(url), document, null);
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        this.hiddenConnectors.addAll(super.getInputConnectors());
        for (WPSIODescription input : this.getProcessDescription().getInputs().values()) {
            this.setUniqueInputIdentifier(input.getIdentifier());
            IInputConnector connector = new InputConnector(this.getUniqueInputIdentifier(input.getIdentifier()), input.getDescription(), this, input.getRuntimeConstraints(), input.getConnectionConstraints(), null);
            this.addInputConnector(connector);
        }
    }

    @Override
    public void initializeOutputConnectors() {
        for (WPSIODescription output : this.getProcessDescription().getOutputs().values()) {
            this.setUniqueOutputIdentifier(output.getIdentifier());
            IOutputConnector connector = new OutputConnector(this.getUniqueOutputIdentifier(output.getIdentifier()), output.getDescription(), this, output.getRuntimeConstraints(), output.getConnectionConstraints());
            this.addOutputConnector(connector);
        }
    }

    @NotNull
    private String getXMLRequest() {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        XMLBuilder builder = new XMLBuilder("wps", "Execute", null, null, null);
        //add xml namespace attributes
        builder.addAttribute("service", "wps");
        builder.addAttribute("version", "1.0.0");
        builder.addAttribute("encoding", "UTF-8");
        builder.addAttribute("xmlns:wps", "http://www.opengis.net/wps/1.0.0");
        builder.addAttribute("xmlns:ows", "http://www.opengis.net/ows/1.1");
        builder.addAttribute("xmlns:ogc", "http://www.opengis.net/ogc");
        builder.addAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        builder.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        builder.addAttribute("xsi:schemaLocation", "http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsExecute_request.xsd");
        //add wps identifier
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, this.getProcessId(), null));
        //add inputs
        builder.addChildNode(getDataInputs());
        //add response
        builder.addChildNode(getResponseForm());
        //return
        return header + builder.toString();
    }

    private XMLBuilder getDataInputs() {
        XMLBuilder builder = new XMLBuilder("wps", "DataInputs", null, null, null);
        for (IInputConnector connector : this.getInputConnectors()) {
            if (connector.getData() != null && !this.isHiddenConnector(connector))
                builder.addChildNode(getInput(connector));
        }
        return builder;
    }

    /**
     * check if input connector should be hidden from XML request
     * @param connector inüput connector
     * @return true, if connector should be hidden
     */
    private boolean isHiddenConnector(IInputConnector connector) {
        return this.hiddenConnectors.contains(connector);
    }

    private XMLBuilder getInput(@NotNull IInputConnector inputConnector) {
        XMLBuilder builder = new XMLBuilder("wps", "Input", null, null, null);
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, this.getInputKeyForIdentifier(inputConnector.getIdentifier()), null));
        builder.addChildNode(getData(inputConnector.getData()));
        return builder;
    }

    private XMLBuilder getData(@Nullable IData data) {
        if (data instanceof URLLiteral)
            return getData((URLLiteral) data);
        if (data instanceof LiteralData)
            return getData((LiteralData) data);
        //TODO add support for complex data
        throw new RuntimeException("complex input is not supported");
    }

    private XMLBuilder getData(URLLiteral data) {
        XMLBuilder builder = new XMLBuilder("wps", "Reference", null, null, null);
        if (data.getIOFormat() != null && data.getIOFormat().getMimetype() != null)
            builder.addAttribute("mimeType", data.getIOFormat().getMimetype());
        if (data.getIOFormat() != null && data.getIOFormat().getSchema() != null)
            builder.addAttribute("schema", data.getIOFormat().getSchema());
        builder.addAttribute("xlink:href", data.resolve().toString());
        builder.addAttribute("method", "GET");
        return builder;
    }

    private String getSchema(IOFormat defaultFormat) {
        return defaultFormat.getSchema();
    }

    private XMLBuilder getData(@NotNull LiteralData data) {
        XMLBuilder builder = new XMLBuilder("wps", "Data", null, null, null);
        builder.addChildNode(getLiteralData(data));
        return builder;
    }

    private XMLBuilder getLiteralData(@NotNull LiteralData data) {
        XMLBuilder builder = new XMLBuilder("wps", "LiteralData", null, data.getLiteralValue(), null);
        //TODO: support literal type matching
//        builder.addAttribute("dataType", getLiteralType(data));
        return builder;
    }

    private String getLiteralType(@NotNull LiteralData data) {
        if (data instanceof DecimalLiteral)
            return "xs:double";
        if (data instanceof LongLiteral)
            return "xs:long";
        if (data instanceof IntegerLiteral)
            return "xs:integer";
        if (data instanceof BooleanLiteral)
            return "xs:boolean";
        else
            return "xs:string";
    }

    private XMLBuilder getResponseForm() {
        XMLBuilder builder = new XMLBuilder("wps", "ResponseForm", null, null, null);
        builder.addChildNode(getResponseDocument());
        return builder;
    }

    private XMLBuilder getResponseDocument() {
        XMLBuilder builder = new XMLBuilder("wps", "ResponseDocument", null, null, null);
        builder.addAttribute("storeExecuteResponse", "false");
        builder.addAttribute("lineage", "false");
        builder.addAttribute("status", "false");
        for (IOutputConnector connector : this.getOutputConnectors()) {
            if (!connector.getIdentifier().getLocalIdentifier().matches(REGEX_IGNORED_OUTPUT))
                builder.addChildNode(getOutput(connector));
        }
        return builder;
    }

    private XMLBuilder getOutput(@NotNull IOutputConnector outputConnector) {
        XMLBuilder builder = new XMLBuilder("wps", "Output", null, null, null);
        builder.addAttribute("asReference", "true");
        builder.addChildNode(new XMLBuilder("ows", "Identifier", null, this.getOutputKeyForIdentifier(outputConnector.getIdentifier()), null));
        return builder;
    }

    private void setOutput(XMLResponse response) throws MalformedURLException {
        List<Node> outputs = response.getNodes(PROCESS_OUTPUT);
        for (Node output : outputs) {
            setOutput(output);
        }
    }

    private void setOutput(Node output) throws MalformedURLException {
        String identifier = null;
        String reference = null;
        String mimeType = null;
        String schema = null;
        NodeList layerNodes = output.getChildNodes();
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Node element = layerNodes.item(i);
            if (element.getNodeName().matches(IO_IDENTIFIER)) {
                identifier = element.getTextContent().trim();
            } else if (element.getNodeName().matches(IO_REFERENCE)) {
                NamedNodeMap attributes = element.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    if (attribute.getNodeName().matches(IO_REFERENCE_ATT))
                        reference = attribute.getNodeValue().trim();
                    else if (attribute.getNodeName().matches(IO_REFERENCE_TYPE))
                        mimeType = attribute.getNodeValue().trim();
                    else if (attribute.getNodeName().matches(IO_REFERENCE_SCHEMA))
                        schema = attribute.getNodeValue().trim();
                }
            }
        }
        if (identifier == null || reference == null)
            throw new RuntimeException("Could not determine output reference");
        //set connector data
        this.setOutput(identifier, new URLLiteral(new URL(reference), new IOFormat(mimeType, schema, null)));
    }

}
