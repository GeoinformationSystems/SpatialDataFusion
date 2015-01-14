package de.tudresden.gis.fusion.manage;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import org.opengis.geometry.BoundingBox;

public class DataUtilities {

	/**
	 * get OSM Overpass resource URI
	 * @param type OSM geometry type (node, way, relation or * (all))
	 * @param tags filter tags
	 * @param bbox bounding box filter
	 * @return OSM Overpass resource URI that can be used to request OSM features according to the input filters
	 * @throws MalformedURLException 
	 */
	public static URI getOSMOverpassResource(String type, Map<String,String> tags, BoundingBox bbox) {
		//check, if feature type is valid
		if(!type.toLowerCase().matches("node|way|relation|\\*"))
			throw new IllegalArgumentException("OSM type must match regex \"node|way|relation|\\*\"");
		//check if bounding box is provided
		if(bbox == null) 
			throw new IllegalArgumentException("OSM bounds must not be null");
		//set xapi base
		String sBase = "http://www.overpass-api.de/api/xapi";
		//set bounds
		String bounds = "[bbox=" + bbox.getMinX() + "," + bbox.getMinY() + "," + bbox.getMaxX() + "," + bbox.getMaxY() + "]";
		//set attributes
		String attributes = "";
		if(tags != null){
			for(Map.Entry<String,String> tag : tags.entrySet()){
				attributes += "[" + tag.getKey() + "=" + tag.getValue() + "]";
			}
		}
		//build URL
		String sURL = sBase + "?" + type + bounds + attributes;
		return URI.create(sURL);
	}
	
	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @param prefixes URI prefixes
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI base, Map<URI,String> prefixes) {
		URI relative = relativizeIdentifier(identifier, base);
		if(identifier.equals(relative) && prefixes != null){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				relative = relativizeIdentifier(identifier, prefix.getKey(), prefix.getValue());
				if(!identifier.equals(relative))
					return relative;
			}
		}
		return relative;
	}
	
	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @param prefixes URI prefix
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI uri, String prefix) {
		if(identifier == null)
			return null;
		if(uri == null)
			return identifier;
		URI relative = uri.relativize(identifier);
		if(identifier.equals(relative) || relative.toString().contains("/"))
			return identifier;
		else
			return URI.create((prefix == null || prefix.length() == 0 ? "" : prefix + ":") + (relative.toString().startsWith("#") ? relative.toString().substring(1) : relative.toString()));
	}

	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI base) {
		return relativizeIdentifier(identifier, base, "");
	}
	
	/**
	 * disassembles a JSON array to String[]
	 * @param jArray JSON array
	 * @return array of Strings
	 */
	public static String[] disassembleJSONArray(String jArray) {
		if(jArray == null || jArray.length() < 3 || jArray.indexOf("[") == -1 || jArray.indexOf("]") == -1)
			throw new IllegalArgumentException("Provided String is no JSON Array (must start with [ and end with ])");
		String sArray = jArray.substring(jArray.indexOf("[")+1, jArray.indexOf("]"));
		sArray = sArray.replace("\"", "").replace("'", "");
		return sArray.split(",");
	}
	
}
