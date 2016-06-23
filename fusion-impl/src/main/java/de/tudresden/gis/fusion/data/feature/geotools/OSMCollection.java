package de.tudresden.gis.fusion.data.feature.geotools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class OSMCollection {

	//OSM feature type definitions
	private final String NODE = "node";
	private final String RELATION = "relation";
	private final String WAY = "way";
	
	private final String ATTRIBUTE = "tag";
	private final String ATTRIBUTE_KEY = "k";
	private final String ATTRIBUTE_VALUE = "v";
	private final String COORD_X = "lon";
	private final String COORD_Y = "lat";
	private final String NODEREF = "nd";
	private final String NODEREF_ID = "ref";
	
	private final String OSMID = "id";
	private final String RELATION_ID = "ref";
	
	private final String RELATION_MEMBER = "member";
	private final String RELATION_ROLE = "role";
	private final String RELATION_TYPE = "type";
	
	//geometry builder using WGS84
	private GeometryFactory geometryFactory = new GeometryFactory();
	
	//collections for the 3 main types
	private HashMap<String,OSMNode> nodes = new HashMap<String,OSMNode>();	
	private HashMap<String,OSMRelation> relations = new HashMap<String,OSMRelation>();	
	private HashMap<String,OSMWay> ways = new HashMap<String,OSMWay>();
	
	/**
	 * constructor
	 * @param is input stream
	 * @throws XMLStreamException
	 */
	public OSMCollection(InputStream is) throws XMLStreamException {
		this.parse(is);
	}
	
	/**
	 * constructor
	 * @param file OSM input file
	 * @throws XMLStreamException
	 * @throws FileNotFoundException 
	 */
	public OSMCollection(File file) throws FileNotFoundException, XMLStreamException {
		this(new FileInputStream(file));
	}
	
	/**
	 * constructor
	 * @param url input URL
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public OSMCollection(URL url) throws XMLStreamException, IOException { 
		this(url.openStream());
	}
	
	/**
	 * return feature collection for nodes
	 * @param is OSM input stream
	 * @param osmType OSM type
	 * @return OSM feature collection
	 */
	public SimpleFeatureCollection getWays() {
		SimpleFeatureType ftype = buildFeatureType(getWayAttributes(), "OSM Ways", LineString.class, DefaultGeographicCRS.WGS84);
		return(buildWaysCollection(ftype));
	}
	
	/**
	 * return feature collection for nodes
	 * @param is OSM input stream
	 * @param osmType OSM type
	 * @return OSM feature collection
	 */
	public SimpleFeatureCollection getNodes() {
		SimpleFeatureType ftype = buildFeatureType(getNodeAttributes(), "OSM Nodes", Point.class, DefaultGeographicCRS.WGS84);
		return(buildNodesCollection(ftype));
	}
	
	/**
	 * build feature type
	 * @param attributes attribute list
	 * @param name fc name
	 * @param geometryClass geometry
	 * @param crs reference system
	 * @return corresponding feature type
	 */
	private SimpleFeatureType buildFeatureType(LinkedList<String> attributes, String name, Class<? extends Geometry> geometryClass, CoordinateReferenceSystem crs) {
		//create builder
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		//set global state
		builder.setName(name);
		builder.setCRS(crs);
		//add attributes
		builder.add(OSMID, String.class);
		builder.add("geometry", geometryClass);
		if(attributes != null && !attributes.isEmpty()){
			for(String attribute : attributes){
				builder.add(attribute, String.class);
			}
		}
		//build and return type
		return builder.buildFeatureType();
	}
	
	/**
	 * build node feature collection
	 * @param ftype feature type
	 * @return feature collection with nodes
	 * @throws IOException 
	 */
	private SimpleFeatureCollection buildNodesCollection(SimpleFeatureType ftype) {
		//create feature list
		LinkedList<SimpleFeature> fList = new LinkedList<SimpleFeature>();
		//get feature builder
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ftype);
		//create features
		for(Entry<String,OSMNode> node : nodes.entrySet()){
			//reset builder
			builder.reset();
			//add geometry
			builder.set("geometry", geometryFactory.createPoint(node.getValue().getCoordinate()));
			//add attributes
			for(Entry<String,String> att : node.getValue().getAttributes().entrySet()){
				builder.set(att.getKey(), att.getValue());
			}
			//add feature to list with OSM id
			fList.add(builder.buildFeature(node.getValue().getId()));
		}
		//return list as feature collection
		return DataUtilities.collection(fList);
	}
	
	/**
	 * build way feature collection
	 * @param ftype feature type
	 * @return feature collection with ways
	 * @throws IOException 
	 */
	private SimpleFeatureCollection buildWaysCollection(SimpleFeatureType ftype) {
		//create feature list
		LinkedList<SimpleFeature> fList = new LinkedList<SimpleFeature>();
		//get feature builder
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ftype);
		//create features
		for(Entry<String,OSMWay> way : ways.entrySet()){
			//reset builder
			builder.reset();
			//add geometry
			builder.set("geometry", way.getValue().getLineString(this.nodes));
			//add attributes
			for(Entry<String,String> att : way.getValue().getAttributes().entrySet()){
				builder.set(att.getKey(), att.getValue());
			}
			//add feature to list with OSM id
			fList.add(builder.buildFeature(way.getValue().getId()));
		}
		//return list as feature collection
		return DataUtilities.collection(fList);
	}
	
	/**
	 * get all node attributes
	 * @return attribute list
	 */
	private LinkedList<String> getNodeAttributes(){
		LinkedList<String> attributes = new LinkedList<String>();
		for(Entry<String,OSMNode> node : nodes.entrySet()){
			for(Entry<String,String> att : node.getValue().getAttributes().entrySet()){
				if(!attributes.contains(att.getKey())) attributes.add(att.getKey());
			}
		}
		return attributes;
	}

	/**
	 * get all way attributes
	 * @return attribute list
	 */
	private LinkedList<String> getWayAttributes(){
		LinkedList<String> attributes = new LinkedList<String>();
		for(Entry<String,OSMWay> way : ways.entrySet()){
			for(Entry<String,String> att : way.getValue().getAttributes().entrySet()){
				if(!attributes.contains(att.getKey())) attributes.add(att.getKey());
			}
		}
		return attributes;
	}
	
	
	/**
	 * parse OSM Input Stream from Overpass API
	 * @param is OSM input
	 * @return feature collection containing OSM features
	 * @throws XMLStreamException 
	 */
	private void parse(InputStream is) throws XMLStreamException {
		XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(is);

	    while(streamReader.hasNext()){
	        streamReader.next();

	        if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT){
	            String elementName = streamReader.getLocalName();
	            if(elementName.equals(NODE)){ parseNodes(streamReader); }
	            else if(elementName.equals(WAY)){ parseWays(streamReader); }
	            else if(elementName.equals(RELATION)){ parseRelations(streamReader); }
	        }
	    }
	}
	
	/**
	 * parse nodes from OSM stream
	 * @param streamReader
	 * @throws XMLStreamException
	 */
	private void parseNodes(XMLStreamReader streamReader) throws XMLStreamException {
			
		//get OSM id
		String osmId = streamReader.getAttributeValue(null, OSMID);
		//init OSM node
		OSMNode node = new OSMNode(
			osmId,
			Double.parseDouble(streamReader.getAttributeValue(null, COORD_X)),
			Double.parseDouble(streamReader.getAttributeValue(null, COORD_Y)));
		//get node attributes
		int attributeCount = streamReader.getAttributeCount();
		for(int i=0; i<attributeCount; i++){
			if(streamReader.getAttributeLocalName(i).equals(OSMID) ||
					streamReader.getAttributeLocalName(i).equals(COORD_X) ||
					streamReader.getAttributeLocalName(i).equals(COORD_Y)) continue;
			node.setAttribute(streamReader.getAttributeLocalName(i), streamReader.getAttributeValue(i));
		}
		//get tag<key,value> attributes
		while(!(streamReader.getEventType() == XMLStreamConstants.END_ELEMENT && streamReader.getLocalName().equals(NODE))){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals(ATTRIBUTE)){
				node.setAttribute(streamReader.getAttributeValue(null, ATTRIBUTE_KEY), streamReader.getAttributeValue(null, ATTRIBUTE_VALUE));
			}
		}
		//put node to nodelist
		nodes.put(osmId, node);
	}
	
	/**
	 * parse relations from OSM stream
	 * @param streamReader
	 * @throws XMLStreamException
	 */
	private void parseRelations(XMLStreamReader streamReader) throws XMLStreamException {

		//get OSM id
		String osmId = streamReader.getAttributeValue(null, OSMID);
		//init OSM node
		OSMRelation relation = new OSMRelation(osmId);
		//get node attributes
		int attributeCount = streamReader.getAttributeCount();
		for(int i=0; i<attributeCount; i++){
			if(streamReader.getAttributeLocalName(i).equals(OSMID)) continue;
			relation.setAttribute(streamReader.getAttributeLocalName(i), streamReader.getAttributeValue(i));
		}
		//get tag<key,value> attributes
		while(!(streamReader.getEventType() == XMLStreamConstants.END_ELEMENT && streamReader.getLocalName().equals(RELATION))){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals(RELATION_MEMBER)){
				relation.addRelationMember(new RelationMember(
						streamReader.getAttributeValue(null, RELATION_TYPE),
						streamReader.getAttributeValue(null, RELATION_ID),
						streamReader.getAttributeValue(null, RELATION_ROLE)));
			}
			else if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals(ATTRIBUTE)){
				relation.setAttribute(streamReader.getAttributeValue(null, ATTRIBUTE_KEY), streamReader.getAttributeValue(null, ATTRIBUTE_VALUE));
			}
		}
		//put node to nodelist
		relations.put(osmId, relation);
	}
	
	/**
	 * parse ways from OSM stream
	 * @param streamReader
	 * @throws XMLStreamException
	 */
	private void parseWays(XMLStreamReader streamReader) throws XMLStreamException {
		
		//get OSM id
		String osmId = streamReader.getAttributeValue(null, OSMID);
		//init OSM node
		OSMWay way = new OSMWay(osmId);
		//get node attributes
		int attributeCount = streamReader.getAttributeCount();
		for(int i=0; i<attributeCount; i++){
			if(streamReader.getAttributeLocalName(i).equals(OSMID)) continue;
			way.setAttribute(streamReader.getAttributeLocalName(i), streamReader.getAttributeValue(i));
		}
		//get tag<key,value> attributes
		while(!(streamReader.getEventType() == XMLStreamConstants.END_ELEMENT && streamReader.getLocalName().equals(WAY))){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals(NODEREF)){
				way.addNodeReference(streamReader.getAttributeValue(null, NODEREF_ID));
			}
			else if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals(ATTRIBUTE)){
				way.setAttribute(streamReader.getAttributeValue(null, ATTRIBUTE_KEY), streamReader.getAttributeValue(null, ATTRIBUTE_VALUE));
			}
		}
		//put node to nodelist
		ways.put(osmId, way);
	}
	
	/**
	 * nested Class for OSM Nodes
	 */
	public class OSMNode {
		private Coordinate coordinate;
		private HashMap<String,String> nodeAttributes;
		private String osmId;
		public OSMNode(String id, double coordx, double coordy) {
			this.osmId = id;
			this.coordinate = new Coordinate(coordx, coordy);
			nodeAttributes = new HashMap<String,String>();
		}
		public HashMap<String,String> getAttributes() { return nodeAttributes; }
		public Coordinate getCoordinate() { return coordinate; }
		public String getId() { return osmId; }
		public void setAttribute(String key, String value) {
			nodeAttributes.put(key, value);
		}
	}
	/**
	 * nested Class for OSM Relations
	 */
	public class OSMRelation {
		private String osmId;
		private HashMap<String,String> relationAttributes;
		private LinkedList<RelationMember> relationMembers;
		public OSMRelation(String id) {
			osmId = id;
			relationAttributes = new HashMap<String,String>();
			relationMembers = new LinkedList<RelationMember>();
		}
		public void addRelationMember(RelationMember member) {
			relationMembers.add(member);
		}
		public String getId() { return osmId; }
		public HashMap<String,String> getRelationAttributes() { return relationAttributes; }
		public LinkedList<RelationMember> getRelationMembers() { return relationMembers; }
		public void setAttribute(String key, String value) {
			relationAttributes.put(key, value);
		}
	}
	/**
	 * nested Class for OSM Ways
	 */
	public class OSMWay {
		private LinkedList<OSMNode> nodeList;
		private LinkedList<String> nodeReferences;
		private String osmId;
		private HashMap<String,String> wayAttributes;
		public OSMWay(String id) {
			this.osmId = id;
			wayAttributes = new HashMap<String,String>();
			nodeReferences = new LinkedList<String>();
			nodeList = new LinkedList<OSMNode>();
		}
		public void addNodeReference(String nodeId) {
			nodeReferences.add(nodeId);
		}
		public HashMap<String,String> getAttributes() { return wayAttributes; }
		public String getId() { return osmId; }
		public LineString getLineString(HashMap<String,OSMNode> nodes) {
			//set node list
			setNodeList(nodes);
			//return null if size of nodeList < 2
			if(this.nodeList.size() < 2) return null;
			//build line string
			LinkedList<Coordinate> coordinateList = new LinkedList<Coordinate>();
			for(OSMNode node : nodeList){
				try {
					coordinateList.add(node.getCoordinate());
				} catch(Exception e){
					e.printStackTrace();
					throw e;
				}
			}			
			return(geometryFactory.createLineString(coordinateList.toArray(new Coordinate[0])));
		}
		public LinkedList<String> getNodeReferences() { return nodeReferences; }
		public void setAttribute(String key, String value) {
			wayAttributes.put(key, value);
		}
		public void setNodeList(HashMap<String,OSMNode> nodes) {				
			for(String node : nodeReferences){
				if(nodes.get(node) == null){
					//TODO: log and handle missing node
				}
				else	
					nodeList.add(nodes.get(node));
			}
		}
	}
	/**
	 * nested class for relation member
	 */
	public class RelationMember {
		private String osmId;
		private String role;
		private String type;
		public RelationMember(String type, String osmId, String role){
			this.type = type;
			this.osmId = osmId;
			this.role = role;
		}
		public String getOsmId() {return osmId;}
		public String getRole() {return role;}
		public String getType() {return type;}
	}
	
}
