package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.*;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IOFormat;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.WPSDescriptionParser;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WPSHandler extends OWSHandler {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
    private final static IIdentifier OUT_DESCRIPTION = new Identifier("OUT_DESCRIPTION");

    private WPSCapabilities wpsCapabilities;
    private WPSProcessDescription processDescriptions;

    private final String REQUEST_DESCRIBEPROCESS = "describeProcess";

    private final String PARAM_IDENTIFIER = "identifier";
    private final String PARAM_SERVICE = "service";
    private final String PARAM_VERSION = "version";
    private final String PARAM_REQUEST = "request";

    private final String SERVICE = "WPS";
    private final String DEFAULT_VERSION = "1.0.0";

    public WPSHandler(String sBaseURL) throws IOException {
        super(sBaseURL);
        this.setService(SERVICE);
        this.setVersion(DEFAULT_VERSION);
        this.wpsCapabilities = getCapabilities();
    }

    /**
     * get WPS capabilities document
     * @return capabilites document
     */
    public WPSCapabilities getCapabilities() throws IOException {
        OWSCapabilities capabilities = super.getCapabilities();
        if(!(capabilities instanceof WPSCapabilities))
            throw new IOException("Could not parse WPS capabilities");
        return (WPSCapabilities) capabilities;
    }

    /**
     * get all processes provided by this WPS instance
     * @return WPS layers
     */
    public Set<String> getSupportedProcesses(){
        return wpsCapabilities != null ? wpsCapabilities.getWPSProcesses() : Collections.emptySet();
    }

    /**
     * check, if certain process is provided
     * @param sProcess input process name
     * @return true, if process is provided by WPS
     */
    public boolean isSupportedProcess(String sProcess){
        return this.getSupportedProcesses().contains(sProcess);
    }

    /**
     * get selected WPS process
     * @return selected WPS process
     */
    public String getProcess() {
        return this.getParameter(PARAM_IDENTIFIER);
    }

    /**
     * select WPS layer
     * @param sProcess WPS process name
     */
    public void setProcess(String sProcess) throws IOException {
        if(!this.isSupportedProcess(sProcess))
            throw new IllegalArgumentException("Process " + sProcess + " is not supported");
        this.setParameter(PARAM_IDENTIFIER, sProcess);
        initProcessDescription();
    }

    /**
     * initialize process description
     * @return process description
     * @throws IOException
     */
    public void initProcessDescription() throws IOException {
        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(URI.create(getDescribeProcessRequest())));
        WPSDescriptionParser parser = new WPSDescriptionParser();
        Map<IIdentifier, IData> output = parser.execute(input);
        if(output == null || !output.containsKey(OUT_DESCRIPTION) || !(output.get(OUT_DESCRIPTION) instanceof WPSProcessDescription))
            throw new IOException("Could not parse WPS process description for " + this.getProcess());
        this.processDescriptions = (WPSProcessDescription) output.get(OUT_DESCRIPTION);
    }

    /**
     * get process description request
     * @return process description request
     */
    public String getDescribeProcessRequest() {
        this.setRequest(REQUEST_DESCRIBEPROCESS);
        return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_VERSION,PARAM_REQUEST,PARAM_IDENTIFIER}, new String[]{});
    }

    /**
     * get process description as json (used by jsPlumb)
     * @return JSON process description
     */
    public JSONObject getJSONProcessDescription() {
        if(this.processDescriptions == null || this.processDescriptions.getProcessDescription(getProcess()) == null)
            return null;
        WPSProcess processDescription = processDescriptions.getProcessDescription(getProcess());
        return new JSONObject()
                .put("identifier", processDescription.getIdentifier())
                .put("title", processDescription.getTitle())
                .put("description", processDescription.getDescription())
                .put("inputs", getJSONProcessDescription(processDescription.getInputs()))
                .put("outputs", getJSONProcessDescription(processDescription.getOutputs()));
    }

    public JSONArray getJSONProcessDescription(Map<IIdentifier,WPSIODescription> ioMap) {
        JSONArray jsonArray = new JSONArray();
        for (WPSIODescription io : ioMap.values()) {
            jsonArray = jsonArray.put(getJSONProcessDescription(io));
        }
        return jsonArray;
    }

    public JSONObject getJSONProcessDescription(WPSIODescription io) {
        return new JSONObject()
                .put("identifier", io.getIdentifier())
                .put("title", io.getTitle())
                .put("defaultFormat", getJSONProcessDescription(io.getDefaultFormat()))
                .put("supportedFormats", getJSONProcessDescription(io.getSupportedFormats()));
    }

    private JSONArray getJSONProcessDescription(Set<IOFormat> ioFormats) {
        JSONArray jsonArray = new JSONArray();
        for (IOFormat io : ioFormats) {
            jsonArray = jsonArray.put(getJSONProcessDescription(io));
        }
        return jsonArray;
    }

    private JSONObject getJSONProcessDescription(IOFormat format) {
        return new JSONObject()
                .put("mimetype", format.getMimetype())
                .put("schema", format.getSchema())
                .put("type", format.getType());
    }

}
