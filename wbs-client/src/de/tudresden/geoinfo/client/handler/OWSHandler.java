package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.OWSCapabilitiesParser;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * OWS Handler
 */
public class OWSHandler {

    private final static String REQUEST_GETCAPABILITIES = "getCapabilities";
    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
    private final static IIdentifier OUT_CAPABILITIES = new Identifier("OUT_CAPABILITIES");
    protected final String PARAM_SERVICE = "service";
    protected final String PARAM_VERSION = "version";
    protected final String PARAM_REQUEST = "request";
    private String sBaseURL;
    private Map<String, String> parameters = new HashMap<>();

    /**
     * constructor
     *
     * @param sBaseURL OWS base URL
     */
    public OWSHandler(String sBaseURL) {
        this.sBaseURL = sBaseURL;
    }

    /**
     * get OWS service URL as String
     *
     * @return OWS service URL
     */
    public String getBaseURL() {
        return sBaseURL;
    }

    /**
     * validate OWS base URL
     *
     * @return false, if URL is null or empty
     */
    public boolean isValidURL() {
        return (this.getBaseURL() != null && !this.getBaseURL().isEmpty());
    }

    /**
     * get OWS request parameter
     *
     * @param key parameter key
     * @return OWS request parameter for key
     */
    public String getParameter(String key) {
        return this.parameters.get(key.toLowerCase());
    }

    /**
     * set OWS request parameter
     *
     * @param key   parameter key
     * @param value parameter value
     */
    public void setParameter(String key, String value) {
        this.parameters.put(key.toLowerCase(), value);
    }

    /**
     * clear list of parameters
     */
    public void clearParameterList() {
        this.parameters.clear();
    }

    /**
     * get kvp parameter string ("key=value")
     *
     * @param key parameter key
     * @return kvp parameter string
     */
    public String getKVPParameter(String key) {
        return key + "=" + this.getParameter(key);
    }

    /**
     * get OWS service type
     *
     * @return OWS service type
     */
    public String getService() {
        return this.getParameter(PARAM_SERVICE);
    }

    /**
     * set OWS service type
     *
     * @param value OWS service type
     */
    public void setService(String value) {
        this.setParameter(PARAM_SERVICE, value);
    }

    /**
     * get OWS request type
     *
     * @return OWS request type
     */
    public String getRequest() {
        return this.getParameter(PARAM_REQUEST);
    }

    /**
     * set OWS request type
     *
     * @param value OWS request type
     */
    public void setRequest(String value) {
        this.setParameter(PARAM_REQUEST, value);
    }

    /**
     * get OWS version
     *
     * @return OWS version
     */
    public String getVersion() {
        return this.getParameter(PARAM_VERSION);
    }

    /**
     * set OWS version
     *
     * @param value OWS version
     */
    public void setVersion(String value) {
        this.setParameter(PARAM_VERSION, value);
    }

    /**
     * get OWS KVP request
     *
     * @param mandatoryKeys mandatory keys for the request
     * @param optionalKeys  optional keys for the request
     * @return OWS request
     */
    public String getKVPRequest(String[] mandatoryKeys, String[] optionalKeys) {
        if (!this.isValidURL())
            throw new IllegalArgumentException("OWS base URL must not be null");
        StringBuilder sBuilder = new StringBuilder().append(this.getBaseURL() + "?");
        if (mandatoryKeys != null) {
            for (String key : mandatoryKeys) {
                if (this.getParameter(key) == null || this.getParameter(key).length() == 0)
                    throw new IllegalArgumentException("KVP parameter " + key + " must not be null");
                sBuilder.append(getKVPParameter(key) + "&");
            }
        }
        if (optionalKeys != null) {
            for (String key : optionalKeys) {
                if (this.getParameter(key) != null && this.getParameter(key).length() != 0)
                    sBuilder.append(getKVPParameter(key) + "&");
            }
        }
        return sBuilder.substring(0, sBuilder.length() - 1);
    }

    /**
     * get capabilities request
     *
     * @return capabilities request
     */
    public String getGetCapabilitiesRequest() {
        this.setRequest(REQUEST_GETCAPABILITIES);
        return getKVPRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST}, new String[]{PARAM_VERSION});
    }

    /**
     * get OWS capabilities document from request
     *
     * @return capabilites implementation
     */
    public OWSCapabilities getCapabilities() throws IOException {
        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(URI.create(getGetCapabilitiesRequest())));
        OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
        Map<IIdentifier, IData> output = parser.execute(input);
        if (output == null || !output.containsKey(OUT_CAPABILITIES) || !(output.get(OUT_CAPABILITIES) instanceof OWSCapabilities))
            throw new IOException("Could not parse OWS capabilities");
        return (OWSCapabilities) output.get(OUT_CAPABILITIES);
    }

}
