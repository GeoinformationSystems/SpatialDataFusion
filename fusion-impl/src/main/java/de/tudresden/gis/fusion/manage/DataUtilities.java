package de.tudresden.gis.fusion.manage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.geometry.BoundingBox;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.namespace.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.namespace.ERDFNamespaces;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.metadata.data.IDescription;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;

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
	 * resolve URI identifier
	 * @param identifier input identifier
	 * @param prefixes RDF prefixes
	 * @return resolved identifier
	 * @throws IOException
	 */
	public static URI resolveIdentifier(String identifier, Map<String,URI> prefixes) throws IOException {
		if(identifier == null)
			return null;
		if(identifier.contains("<"))
			return URI.create(identifier.replace("<","").replace(">",""));
		String[] parts = identifier.split(":");
		if(parts.length != 2)
			throw new IOException("invalid IRI in " + identifier);
		if(!prefixes.containsKey(parts[0]))
			throw new IOException("no prefix defined for " + identifier);
		return URI.create(prefixes.get(parts[0]) + parts[1]);
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
	
	/**
	 * create node set out of node
	 * @param node input not
	 * @return node set
	 */
	public static Set<INode> toSet(INode node){
		Set<INode> set = new HashSet<INode>();
		set.add(node);
		return set;
	}
	
	/**
	 * create set of nodes from of data array
	 * @param array input data array
	 * @return output node set
	 */
	public static Set<INode> toSet(IData[] array){
		HashSet<INode> nodes = new HashSet<INode>();
		for(IData data : array){
			nodes.add(data.getRDFRepresentation());
		}
		return nodes;
	}
	
	/**
	 * create set out of array
	 * @param array input array
	 * @return output set
	 */
	public static <T> Set<T> toSet(T[] array){
		return new HashSet<T>(Arrays.asList(array));
	}
	
	/**
	 * create node set from data collection
	 * @param collection data collection
	 * @return node set
	 */
	public static Set<INode> dataCollectionToNodeSet(Collection<? extends IData> collection){
		if(collection == null)
			return null;
		Set<INode> set = new HashSet<INode>();
		for(IData data : collection){
			set.add(data.getRDFRepresentation());
		}
		return set;
	}
	
	/**
	 * create node set from collection of descriptions
	 * @param collection descriptions
	 * @return node set
	 */
	public static Set<INode> descriptionsToNodeSet(Collection<? extends IDescription> descriptions){
		if(descriptions == null)
			return null;
		Set<INode> set = new HashSet<INode>();
		for(IDescription desc : descriptions){
			set.add(desc.getRDFRepresentation());
		}
		return set;
	}

	/**
	 * check if resources are provided by standard namespaces
	 * @param identifier input identifier
	 * @return output identifiable resource
	 */
	public static IIdentifiableResource resolveResource(IIRI identifier) {
		IIdentifiableResource resource = ERDFNamespaces.resource4Identifier(identifier);
		if(resource == null)
			resource = EFusionNamespace.resource4Identifier(identifier);		
		if(resource != null)
			return resource;		
		return new IdentifiableResource(identifier);
	}
	
	/**
	 * get GeoTools SimpleFeatureCollection from IFeaturecollection
	 * @param features input features
	 * @return GeoTools feature collection
	 */
	public static SimpleFeatureCollection getGTFeatureCollection(IFeatureCollection features){
		if(features instanceof GTFeatureCollection)
			return ((GTFeatureCollection) features).getSimpleFeatureCollection();
		
		//TODO: implement transformation based on IFeatureCollection methods
		return null;
	}
	
	/**
	 * encode String literal
	 * @param sLiteral literal string
	 * @return encoded literal (type based on RegEx)
	 */
	public static ISimpleData encodeLiteral(String sLiteral){
		//check boolean
		if(sLiteral.matches("^(?i)(true|false)"))
			return new BooleanLiteral(Boolean.parseBoolean(sLiteral));
		//check integer
		//TODO: separate long from int
		if(sLiteral.matches("/^\\d+$"))
			return new IntegerLiteral(Integer.parseInt(sLiteral));
		//check decimal
		if(sLiteral.matches("^\\d+\\.?\\d+$"))
			return new DecimalLiteral(Double.parseDouble(sLiteral));
		//final: return string literal
		return new StringLiteral(sLiteral);
	}
	
	/**
	 * create temporary file
	 * @param name file name
	 * @param suffix file suffix
	 * @return temp file
	 */
	public static File createTmpFile(String name, String suffix){
		try {
			return File.createTempFile(name, suffix);
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		}
	}
	
	/**
	 * get measurement value from literal
	 * @param literal input literal
	 * @return measurement value
	 */
	public static IMeasurementValue<?> getMeasurementValue(ITypedLiteral literal){
		if(literal instanceof IMeasurementValue)
			return (IMeasurementValue<?>) literal;
		else
			return new StringLiteral(literal.getIdentifier());
	}
	
	/**
	 * get object from object set
	 * @param objectSet object set
	 * @param resource resource to search for
	 * @param clazz target class that needs to be matched
	 * @return object from object set
	 * @throws IOException 
	 */
	public static INode getSingleFromObjectSet(Map<IIdentifiableResource,Set<INode>> objectSet, IIdentifiableResource resource, Class<? extends INode> clazz, boolean mustHave) throws IOException{
		//get objects
		Set<INode> objects = objectSet.get(resource);
		//check for single object
		if(objects == null || objects.size() != 1){
			if(mustHave)
				throw new IOException("Missing or multiple definition for " + resource.getIdentifier().toString());
			else return null;
		}
		//check for object
		INode object = objects.iterator().next();
		if(!clazz.isAssignableFrom(object.getClass()))
			throw new IOException(object.getClass().getSimpleName() + " is not assignable from " + clazz.getSimpleName());
		return object;
	}
	
	/**
	 * get objects from object set
	 * @param objectSet object set
	 * @param resource resource to search for
	 * @param clazz target class that needs to be matched
	 * @return objects from object set
	 * @throws IOException 
	 */
	public static Set<INode> getMultipleFromObjectSet(Map<IIdentifiableResource,Set<INode>> objectSet, IIdentifiableResource resource, Class<? extends INode> clazz, boolean mustHave) throws IOException{
		//get objects
		Set<INode> objects = objectSet.get(resource);
		//check for multiple object
		if(objects.size() < 1){
			if(mustHave)
				throw new IOException("Missing definition for " + resource.getIdentifier().toString());
			else return null;
		}
		//check objects
		for(INode object : objects){
			if(!clazz.isAssignableFrom(object.getClass()))
				throw new IOException("Object " + resource.getIdentifier().toString() + " cannot be assigned to " + clazz.getSimpleName());
			}
		return objects;
	}
	
}
