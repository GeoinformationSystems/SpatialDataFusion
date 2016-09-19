package de.tud.fusion.data.feature.osm;

import java.util.HashMap;
import java.util.Map;

import de.tud.fusion.data.DataUtilities;

/**
 * OSM XML property set
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class XMLPropertySet {
	
	public static final String OSM_PROPERTY_ID = "id";
	
	private Map<String,Object> tags;
	private Map<String,Object> properties;
	
	/**
	 * constructor
	 * @param tags OSM tags
	 * @param properties OSM properties
	 */
	public XMLPropertySet(Map<String,String> properties, Map<String,String> tags){
		setTags(tags);
		setProperties(properties);
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
	 * @param tags input properties
	 */
	private void setProperties(Map<String,String> properties) {
		this.properties = getMapEntries(properties);
	}
	
	/**
	 * set OSM map entries
	 * @param inputs input entries
	 * @param target target map
	 * @return 
	 */
	private Map<String,Object> getMapEntries(Map<String,String> inputs){
		Map<String,Object> map = new HashMap<String,Object>();
		for(Map.Entry<String,String> input : inputs.entrySet()){
			map.put(input.getKey(), DataUtilities.parseObjectFromString(input.getValue()));
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
	 * get OSM element identifier
	 * @return element identifier
	 */
	public String getIdentifier(){
		return properties.get(OSM_PROPERTY_ID).toString();
	}
	
}
