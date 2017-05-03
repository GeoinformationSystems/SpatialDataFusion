package de.tud.fusion;

import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
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
     * set request parameter
     *
     * @param key   parameter key
     * @param value parameter value
     */
    public void setParameter(@NotNull String key, @Nullable String value) {
        if(value != null && !value.isEmpty())
            this.parameters.put(key.toLowerCase(), value);
    }

    /**
     * set request parameter
     *
     * @param key   parameter key
     */
    public @Nullable String removeParameter(String key) {
        return this.parameters.remove(key);
    }

    /**
     * get kvp parameter string ("key=value")
     *
     * @param key parameter key
     * @return kvp parameter string
     */
    private @Nullable String getKVPString(@NotNull String key) {
        if(!this.parameters.containsKey(key))
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
    public URLLiteral getKVPRequest(@NotNull String[] mandatoryKeys, @NotNull String[] optionalKeys) throws MalformedURLException {
        StringBuilder sBuilder = new StringBuilder().append(this.base.toString()).append("?");
        for (String key : mandatoryKeys) {
            if (this.getParameter(key) == null || this.getParameter(key).length() == 0)
                throw new IllegalArgumentException("KVP parameter " + key + " must not be null");
            sBuilder.append(this.getKVPString(key) + "&");
        }
        for (String key : optionalKeys) {
            if (this.getParameter(key) != null && this.getParameter(key).length() != 0)
                sBuilder.append(this.getKVPString(key) + "&");
        }
        return new URLLiteral(new URL(sBuilder.substring(0, sBuilder.length() - 1)));
    }

}
