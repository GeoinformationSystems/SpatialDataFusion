package de.tud.fusion.operation.retrieval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.tud.fusion.data.feature.osm.OSMFeature;
import de.tud.fusion.data.feature.osm.OSMFeatureCollection;
import de.tud.fusion.data.feature.osm.OSMNode;
import de.tud.fusion.data.feature.osm.OSMRelation;
import de.tud.fusion.data.feature.osm.OSMRelation.OSMRelationMember;
import de.tud.fusion.data.feature.osm.OSMWay;
import de.tud.fusion.data.feature.osm.XMLPropertySet;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.InputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class OSMXMLParser extends AbstractOperation {

public final static String PROCESS_ID = OSMXMLParser.class.getSimpleName();
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;
	
	//OSM feature type definitions
	private final String TYPE_NODE = "node";
	private final String TYPE_RELATION = "relation";
	private final String TYPE_WAY = "way";
	private final String TYPE_REGEX = TYPE_NODE + "|" + TYPE_WAY + "|" + TYPE_RELATION;
	//OSM attribute definitions
	private final String ATTRIBUTE = "tag";
	private final String ATTRIBUTE_KEY = "k";
	private final String ATTRIBUTE_VALUE = "v";
	private final String NODEREF = "nd";
	private final String NODEREF_ID = "ref";
	private final String RELATION_ID = "ref";	
	private final String RELATION_MEMBER = "member";
	private final String RELATION_TYPE = "type";
	private final String RELATION_ROLE = "role";
	
	/**
	 * constructor
	 */
	public OSMXMLParser() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		//get data
		URL resourceURL;
		try {
			resourceURL = ((URILiteral) resourceConnector.getData()).resolve().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("OSM source is no valid URL", e);
		}
		//parse features
		OSMFeatureCollection<OSMFeature> features;
		try {
			features = parseOSM(resourceURL);
		} catch (IOException | XMLStreamException e) {
			throw new RuntimeException("Could not parse OSM XML source", e);
		}
		//set output connector
		setOutputConnector(OUT_FEATURES, features);
	}
	
	private OSMFeatureCollection<OSMFeature> parseOSM(URL resourceURL) throws IOException, XMLStreamException {
		//parse HTTP connection
		if(resourceURL.getProtocol().toLowerCase().startsWith("http"))
			return parseOSMFromHTTP(resourceURL);		
		//parse file
		else if(resourceURL.getProtocol().toLowerCase().startsWith("file"))
			return parseOSMFromFile(resourceURL);
		else
			throw new IllegalArgumentException("Unsupported OSM source");
	}
	
	private OSMFeatureCollection<OSMFeature> parseOSMFromHTTP(URL resourceURL) throws IOException, XMLStreamException {
		HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
		urlConnection.connect();
		return parse(resourceURL.toString(), urlConnection.getInputStream());
	}
	
	private OSMFeatureCollection<OSMFeature> parseOSMFromFile(URL resourceURL) throws FileNotFoundException, XMLStreamException {
		File file = new File(resourceURL.getFile());
		if(!file.exists() || file.isDirectory())
			throw new IllegalArgumentException("Cannot read OSM resource");
		return parse(resourceURL.toString(), new FileInputStream(file));
	}

	private OSMFeatureCollection<OSMFeature> parse(String identifier, InputStream stream) throws XMLStreamException {
		Map<String,XMLPropertySet> featureMap = new HashMap<String,XMLPropertySet>();
		XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		//parse OSM features
	    while(streamReader.hasNext()){
	        streamReader.next();
	        //parse, if new XML element
	        if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT){
	            String elementName = streamReader.getLocalName();
	            if(elementName.matches(TYPE_REGEX)){
	            	XMLPropertySet propertySet = parseXMLPropertySet(streamReader);
	            	featureMap.put(propertySet.getIdentifier(), propertySet);
	            }
	        }
	    }
	    //init OSM collection
	    return initOSMCollection(identifier, featureMap);
	}

	private XMLPropertySet parseXMLPropertySet(XMLStreamReader streamReader) throws XMLStreamException {
		Map<String,String> properties = new HashMap<String,String>();
		Map<String,String> tags = new HashMap<String,String>();
		//get attributes		
		int attributeCount = streamReader.getAttributeCount();
		for(int i=0; i<attributeCount; i++){
			properties.put(streamReader.getAttributeLocalName(i), streamReader.getAttributeValue(i));
		}
		//get tags
		while(!(streamReader.getEventType() == XMLStreamConstants.END_ELEMENT && streamReader.getLocalName().matches(TYPE_REGEX))){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT){
				String elementName = streamReader.getLocalName();
				//get attribute tag
				if(elementName.equals(ATTRIBUTE)){
					tags.put(streamReader.getAttributeValue(null, ATTRIBUTE_KEY), streamReader.getAttributeValue(null, ATTRIBUTE_VALUE));
				}
				//get relation member
				if(elementName.equals(RELATION_MEMBER)){
					if(!tags.containsKey(RELATION_MEMBER))
						tags.put(RELATION_MEMBER, 
								streamReader.getAttributeValue(null, RELATION_ID) + "," +
								streamReader.getAttributeValue(null, RELATION_TYPE) + "," +
								streamReader.getAttributeValue(null, RELATION_ROLE));
					else
						tags.put(RELATION_MEMBER, tags.get(RELATION_MEMBER).concat(";" + 
								streamReader.getAttributeValue(null, RELATION_ID) + "," +
								streamReader.getAttributeValue(null, RELATION_TYPE) + "," +
								streamReader.getAttributeValue(null, RELATION_ROLE)));
				}
				//get node reference
				if(elementName.equals(NODEREF)){
					if(!tags.containsKey(NODEREF))
						tags.put(NODEREF, streamReader.getAttributeValue(null, NODEREF_ID));
					else
						tags.put(NODEREF, tags.get(NODEREF).concat(";" + streamReader.getAttributeValue(null, NODEREF_ID)));
				}
			}
		}		
		//init feature
		return new XMLPropertySet(properties, tags);
	}

	private OSMFeatureCollection<OSMFeature> initOSMCollection(String identifier, Map<String,XMLPropertySet> featureMap) {
		Map<String,OSMFeature> map = new HashMap<String,OSMFeature>();
		//first run: add nodes
		for(Map.Entry<String,XMLPropertySet> entry : featureMap.entrySet()){
			if(entry.getValue().getProperties() != null && entry.getValue().getProperties().containsKey(OSMNode.OSM_PROPERTY_LAT))
				map.put(entry.getValue().getIdentifier(), new OSMNode(entry.getValue(), null, null));
		}
		//second run: add ways
		for(Map.Entry<String,XMLPropertySet> entry : featureMap.entrySet()){
			if(entry.getValue().getTags() != null && entry.getValue().getTags().containsKey(NODEREF)){
				String[] refs = entry.getValue().getTags().get(NODEREF).toString().split(";");
				LinkedList<OSMNode> nodes = new LinkedList<OSMNode>();
				for(String ref : refs){
					if(map.containsKey(ref))
						nodes.add((OSMNode) map.get(ref));						
					//TODO: handle missing nodes
				}
				if(nodes.size() < 2)
					continue; // cannot create way, if there are less than 2 nodes
				map.put(entry.getValue().getIdentifier(), new OSMWay(entry.getValue(), null, nodes, null));
			}
		}
		//third run: add relations
		for(Map.Entry<String,XMLPropertySet> entry : featureMap.entrySet()){
			if(entry.getValue().getTags() != null && entry.getValue().getTags().containsKey(RELATION_MEMBER)){
				String[] members = entry.getValue().getTags().get(RELATION_MEMBER).toString().split(";");
				Set<OSMRelationMember> relationMembers = new HashSet<OSMRelationMember>();
				for(String member : members){
					String[] refRole = member.split(",", -1);
					if(map.containsKey(refRole[0]))
						relationMembers.add(new OSMRelationMember(map.get(refRole[0]), refRole[1]));
					//TODO: handle missing members 
				}
				if(relationMembers.size() < 2)
					continue; // cannot create relation, if there are less than 2 members
				map.put(entry.getValue().getIdentifier(), new OSMRelation(entry.getValue(), null, relationMembers, null));
			}
		}
		//return
		return new OSMFeatureCollection<OSMFeature>(identifier, map.values(), null);
	}

	@Override
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_RESOURCE, IN_RESOURCE, "Link to input XML",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null,
				null));	
		//return
		return inputConnectors;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_FEATURES, OUT_FEATURES, "Output OSM feature collection",
				new IDataConstraint[]{
						new BindingConstraint(OSMFeatureCollection.class),
						new MandatoryConstraint()},
				null));	
		//return
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "OSM XML Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for OSM XML format";
	}
	
}
