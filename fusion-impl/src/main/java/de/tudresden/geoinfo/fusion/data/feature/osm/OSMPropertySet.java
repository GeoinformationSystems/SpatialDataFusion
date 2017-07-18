package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * OSM XML property set
 */
public class OSMPropertySet extends ResourceIdentifier {

    private static final String OSM_PROPERTY_ID = "id";

    private Map<String, Object> tags;
    private Map<String, Object> properties;

    /**
     * constructor
     *
     * @param tags       OSM tags
     * @param properties OSM properties
     */
    public OSMPropertySet(@NotNull Map<String, String> properties, @NotNull Map<String, String> tags) {
        super(properties.get(OSM_PROPERTY_ID), null);
        setTags(tags);
        setProperties(properties);
    }

    /**
     * set OSM map entries
     *
     * @param inputs input entries
     * @return map entries
     */
    private Map<String, Object> getMapEntries(@NotNull Map<String, String> inputs) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> input : inputs.entrySet()) {
            map.put(input.getKey(), LiteralData.parseTypedLiteral(input.getValue()));
        }
        return map;
    }

    /**
     * get OSM tags
     *
     * @return tags
     */
    @NotNull
    public Map<String, Object> getTags() {
        return tags;
    }

    /**
     * set OSM tags
     *
     * @param tags input tags
     */
    private void setTags(Map<String, String> tags) {
        this.tags = getMapEntries(tags);
    }

    /**
     * get OSM properties
     *
     * @return properties
     */
    @NotNull
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * set OSM properties
     *
     * @param properties OSM properties
     */
    private void setProperties(@NotNull Map<String, String> properties) {
        this.properties = getMapEntries(properties);
    }

    /**
     * get OSM properties
     *
     * @return properties
     */
    @NotNull
    public Object getProperty(@NotNull String key) {
        return properties.get(key);
    }

    /**
     * check if properties contains key
     *
     * @param key property key
     * @return property for key
     */
    public boolean containsKey(@NotNull String key) {
        return properties.containsKey(key);
    }

}
