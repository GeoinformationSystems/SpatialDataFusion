package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.*;
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
    private final static String REQUEST_DESCRIBEPROCESS = "describeProcess";
    private final static String PARAM_IDENTIFIER = "identifier";
    private final static String SERVICE = "WPS";
    private final static String DEFAULT_VERSION = "1.0.0";
    private WPSCapabilities wpsCapabilities;
    private WPSProcessDescriptions processDescriptions;

    public WPSHandler(String sBaseURL) throws IOException {
        super(sBaseURL);
        this.setService(SERVICE);
        this.setVersion(DEFAULT_VERSION);
        this.wpsCapabilities = getCapabilities();
    }

    /**
     * get WPS capabilities document
     *
     * @return capabilites document
     */
    public WPSCapabilities getCapabilities() throws IOException {
        OWSCapabilities capabilities = super.getCapabilities();
        if (!(capabilities instanceof WPSCapabilities))
            throw new IOException("Could not parse WPS capabilities");
        return (WPSCapabilities) capabilities;
    }

    /**
     * get all processes provided by this WPS instance
     *
     * @return WPS layers
     */
    public Set<String> getSupportedProcesses() {
        return wpsCapabilities != null ? wpsCapabilities.getWPSProcesses() : Collections.emptySet();
    }

    /**
     * check, if certain process is provided
     *
     * @param sProcess input process name
     * @return true, if process is provided by WPS
     */
    public boolean isSupportedProcess(String sProcess) {
        return this.getSupportedProcesses().contains(sProcess);
    }

    /**
     * get selected WPS process
     *
     * @return selected WPS process
     */
    public String getProcess() {
        return this.getParameter(PARAM_IDENTIFIER);
    }

    /**
     * select WPS layer
     *
     * @param sProcess WPS process name
     */
    public void setProcess(String sProcess) throws IOException {
        if (!this.isSupportedProcess(sProcess))
            throw new IllegalArgumentException("Process " + sProcess + " is not supported");
        this.setParameter(PARAM_IDENTIFIER, sProcess);
        initProcessDescription();
    }

    /**
     * initialize process description
     *
     * @return process description
     * @throws IOException
     */
    public void initProcessDescription() throws IOException {
        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(URI.create(getDescribeProcessRequest())));
        WPSDescriptionParser parser = new WPSDescriptionParser();
        Map<IIdentifier, IData> output = parser.execute(input);
        if (output == null || !output.containsKey(OUT_DESCRIPTION) || !(output.get(OUT_DESCRIPTION) instanceof WPSProcessDescriptions))
            throw new IOException("Could not parse WPS process description for " + this.getProcess());
        this.processDescriptions = (WPSProcessDescriptions) output.get(OUT_DESCRIPTION);
    }

    public WPSProcessDescription getProcessDescription() {
        return processDescriptions.getProcessDescription(getProcess());
    }

    /**
     * get process description request
     *
     * @return process description request
     */
    public String getDescribeProcessRequest() {
        this.setRequest(REQUEST_DESCRIBEPROCESS);
        return getKVPRequest(new String[]{PARAM_SERVICE, PARAM_VERSION, PARAM_REQUEST, PARAM_IDENTIFIER}, new String[]{});
    }

    /**
     * get process description as json (used by jsPlumb)
     *
     * @return JSON process description
     */
    public JSONObject getJSONProcessDescription() {
        if (this.processDescriptions == null || this.processDescriptions.getProcessDescription(getProcess()) == null)
            return null;
        return new JSONObject()
                .put("identifier", getProcessDescription().getIdentifier())
                .put("title", getProcessDescription().getTitle())
                .put("description", getProcessDescription().getDescription())
                .put("inputs", getJSONProcessDescription(getProcessDescription().getInputs()))
                .put("outputs", getJSONProcessDescription(getProcessDescription().getOutputs()));
    }

    /**
     * get process io descriptions as json
     *
     * @param ioMap io descriptions
     * @return JSON io process descriptions
     */
    public JSONArray getJSONProcessDescription(Map<IIdentifier, WPSIODescription> ioMap) {
        JSONArray jsonArray = new JSONArray();
        for (WPSIODescription io : ioMap.values()) {
            jsonArray = jsonArray.put(getJSONProcessDescription(io));
        }
        return jsonArray;
    }

    /**
     * get process io description as json
     *
     * @param io io description
     * @return JSON io process description
     */
    public JSONObject getJSONProcessDescription(WPSIODescription io) {
        return new JSONObject()
                .put("identifier", io.getIdentifier())
                .put("title", io.getTitle())
                .put("defaultFormat", getJSONProcessDescription(io.getDefaultFormat()))
                .put("supportedFormats", getJSONProcessDescription(io.getSupportedFormats()));
    }

    /**
     * get io formats description as json
     *
     * @param ioFormats io formats description
     * @return JSON io formats description
     */
    private JSONArray getJSONProcessDescription(Set<WPSIOFormat> ioFormats) {
        JSONArray jsonArray = new JSONArray();
        for (WPSIOFormat io : ioFormats) {
            jsonArray = jsonArray.put(getJSONProcessDescription(io));
        }
        return jsonArray;
    }

    /**
     * get io format description as json
     *
     * @param ioFormat io format description
     * @return JSON io format description
     */
    private JSONObject getJSONProcessDescription(WPSIOFormat ioFormat) {
        return new JSONObject()
                .put("mimetype", ioFormat.getMimetype())
                .put("schema", ioFormat.getSchema())
                .put("type", ioFormat.getType());
    }

}
