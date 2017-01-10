package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 * OSM XML property set
 */
public class OSMPropertySet {
	
	private static final String OSM_PROPERTY_ID = "id";
	
	private Map<String,Object> tags;
	private Map<String,Object> properties;
	private IIdentifier identifier;
	
	/**
	 * constructor
	 * @param tags OSM tags
	 * @param properties OSM properties
	 */
	public OSMPropertySet(Map<String,String> properties, Map<String,String> tags){
		setTags(tags);
		setProperties(properties);
		this.identifier = new Identifier(properties.get(OSM_PROPERTY_ID));
	}
	
	/**
	 * set OSM tags
	 * @param tags input tags
	 */
	private void setTags(Map<String,String> tags) {
		this.tags = getMapEntries(tags);
	}
	
	/**
	 * set OSM properties
	 * @param properties OSM properties
	 */
	private void setProperties(Map<String,String> properties) {
		this.properties = getMapEntries(properties);
	}
	
	/**
	 * set OSM map entries
	 * @param inputs input entries
	 * @return 
	 */
	private Map<String,Object> getMapEntries(Map<String,String> inputs){
		Map<String,Object> map = new HashMap<>();
		for(Map.Entry<String,String> input : inputs.entrySet()){
			map.put(input.getKey(), LiteralData.parseObjectFromString(input.getValue()));
		}
		return map;
	}
	
	/**
	 * get OSM tags
	 * @return tags
	 */
	public Map<String,Object> getTags() {
		return tags;
	}
	
	/**
	 * get OSM properties
	 * @return properties
	 */
	public Map<String,Object> getProperties() {
		return properties;
	}

	/**
	 * get OSM properties
	 * @return properties
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}

    /**
     * check if properties contains key
     * @param key property key
     * @return property for key
     */
	public boolean containsKey(String key){
	    return properties.containsKey(key);
    }

	/**
	 * get OSM element identifier
	 * @return element identifier
	 */
	public IIdentifier getIdentifier(){
		return identifier;
	}
	
}
