package de.tud.fusion;

import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * OWS Handler
 */
public class KVPRequestBuilder {

    private URLLiteral base;
    private Map<String, String> parameters = new HashMap<>();

    /**
     * constructor
     *
     * @param base base URL
     */
    public KVPRequestBuilder(@NotNull URLLiteral base) {
        this.base = base;
    }

    /**
     * get base URL
     *
     * @return base URL
     */
    public @NotNull URLLiteral getBase() {
        return base;
    }

    /**
     * get OWS request parameter
     *
     * @param key parameter key
     * @return OWS request parameter for key
     */
    public @Nullable String getParameter(@NotNull String key) {
        return this.parameters.get(key.toLowerCase());
    }

    /**
     * get OWS request parameter
     *
     * @param key parameter key
     * @return OWS request parameter for key
     */
    public @NotNull String getMandatoryParameter(@NotNull String key) {
        String parameter = this.parameters.get(key.toLowerCase());
        if(parameter == null)
            throw new IllegalArgumentException("Mandatory constraint violation for key " + key);
        return parameter;
    }

    /**
     * set request parameter
     *
     * @param key   parameter key
     * @param value parameter value
     */
    public void setParameter(@NotNull String key, @Nullable String value) {
        if (value != null && !value.isEmpty())
            this.parameters.put(key.toLowerCase(), value);
    }

    /**
     * set request parameter
     *
     * @param key parameter key
     */
    public void removeParameter(String key) {
        this.parameters.remove(key);
    }

    /**
     * get kvp parameter string ("key=value")
     *
     * @param key parameter key
     * @return kvp parameter string
     */
    private @Nullable String getKVPString(@NotNull String key) {
        String value = this.getParameter(key);
        if (value == null || value.isEmpty())
            return null;
        return key + "=" + this.getParameter(key);
    }

    /**
     * get KVP request
     *
     * @param mandatoryKeys mandatory keys for the request
     * @param optionalKeys  optional keys for the request
     * @return KVP request
     */
    public URLLiteral getKVPRequest(@NotNull String[] mandatoryKeys, @NotNull String[] optionalKeys) {
        StringBuilder sBuilder = new StringBuilder().append(this.base.toString()).append("?");
        for (String key : mandatoryKeys) {
            String kvpString = this.getKVPString(key);
            if (kvpString == null)
                throw new IllegalArgumentException("KVP parameter " + key + " must not be null");
            sBuilder.append(kvpString).append("&");
        }
        for (String key : optionalKeys) {
            String kvpString = this.getKVPString(key);
            if (kvpString != null)
                sBuilder.append(kvpString).append("&");
        }
        return new URLLiteral(sBuilder.substring(0, sBuilder.length() - 1));
    }

}
