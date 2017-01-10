package de.tudresden.geoinfo.fusion.operation.retrieval;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.Graph;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.osm.*;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.IRole;
import de.tudresden.geoinfo.fusion.data.relation.RelationType;
import de.tudresden.geoinfo.fusion.data.relation.Role;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class OSMXMLParser extends AbstractOperation {

	private static final IIdentifier PROCESS = new Identifier(OSMXMLParser.class.getSimpleName());

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

	private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
    private final static IIdentifier OUT_RELATIONS = new Identifier("OUT_RELATIONS");

	//OSM feature type definitions
	private final static String TYPE_NODE = "node";
	private final static String TYPE_RELATION = "relation";
	private final static String TYPE_WAY = "way";
	private final static String TYPE_REGEX = TYPE_NODE + "|" + TYPE_WAY + "|" + TYPE_RELATION;
	//OSM attribute definitions
	private final static String ATTRIBUTE = "tag";
	private final static String ATTRIBUTE_KEY = "k";
	private final static String ATTRIBUTE_VALUE = "v";
	private final static String NODEREF = "nd";
	private final static String NODEREF_ID = "ref";
	private final static String RELATION_ID = "ref";
	private final static String RELATION_MEMBER = "member";
	private final static String RELATION_TYPE = "type";
	private final static String RELATION_ROLE = "role";
	
	/**
	 * constructor
	 */
	public OSMXMLParser() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		//get data
		URI resourceURI = ((URILiteral) resourceConnector.getData()).resolve();
		//init features
		OSMFeatureCollection<OSMVectorFeature> features;
		//init relations
        Graph<OSMRelation> relations;
        //parse
		try {
            Map<String,OSMPropertySet> featureMap = parseOSM(resourceURI.toURL());
            features = initOSMCollection(new Identifier(resourceURI), featureMap);
            relations = initOSMRelations(new Identifier(resourceURI), featureMap, features);
		} catch (IOException | XMLStreamException e) {
			throw new RuntimeException("Could not parse OSM XML source", e);
		}
		//set output connector
		connectOutput(OUT_FEATURES, features);
        connectOutput(OUT_RELATIONS, relations);
	}

    private Map<String,OSMPropertySet> parseOSM(URL resourceURL) throws IOException, XMLStreamException {
		//parse HTTP connection
		if(resourceURL.getProtocol().toLowerCase().startsWith("http"))
			return parseOSMFromHTTP(resourceURL);		
		//parse file
		else if(resourceURL.getProtocol().toLowerCase().startsWith("file"))
			return parseOSMFromFile(resourceURL);
		else
			throw new IllegalArgumentException("Unsupported OSM source");
	}
	
	private Map<String,OSMPropertySet> parseOSMFromHTTP(URL resourceURL) throws IOException, XMLStreamException {
		HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
		urlConnection.connect();
		return parse(urlConnection.getInputStream());
	}
	
	private Map<String,OSMPropertySet> parseOSMFromFile(URL resourceURL) throws FileNotFoundException, XMLStreamException {
		File file = new File(resourceURL.getFile());
		if(!file.exists() || file.isDirectory())
			throw new IllegalArgumentException("Cannot read OSM resource");
		return parse(new FileInputStream(file));
	}

	private Map<String,OSMPropertySet> parse(InputStream stream) throws XMLStreamException {
		Map<String,OSMPropertySet> featureMap = new HashMap<>();
		XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		//parse OSM features
	    while(streamReader.hasNext()){
	        streamReader.next();
	        //parse, if new XML element
	        if(streamReader.getEventType() == XMLStreamConstants.START_ELEMENT){
	            String elementName = streamReader.getLocalName();
	            if(elementName.matches(TYPE_REGEX)){
	            	OSMPropertySet propertySet = parseXMLPropertySet(streamReader);
	            	featureMap.put(propertySet.getIdentifier().toString(), propertySet);
	            }
	        }
	    }
	    //init OSM collection
	    return featureMap;
	}

	private OSMPropertySet parseXMLPropertySet(XMLStreamReader streamReader) throws XMLStreamException {
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
		return new OSMPropertySet(properties, tags);
	}

	private OSMFeatureCollection<OSMVectorFeature> initOSMCollection(IIdentifier identifier, Map<String,OSMPropertySet> featureMap) {
        Map<String,OSMVectorFeature> map = new HashMap<>();
        //first run: add nodes
        for (Map.Entry<String, OSMPropertySet> entry : featureMap.entrySet()) {
            if (entry.getValue().getProperties() != null && entry.getValue().getProperties().containsKey(OSMNode.OSM_PROPERTY_LAT) && entry.getValue().getProperties().containsKey(OSMNode.OSM_PROPERTY_LON))
                map.put(entry.getValue().getIdentifier().toString(), new OSMNode(entry.getValue(), null, null));
        }
        //second run: add ways
        for (Map.Entry<String,OSMPropertySet> entry : featureMap.entrySet()) {
            if (entry.getValue().getTags() != null && entry.getValue().getTags().containsKey(NODEREF)) {
                String[] refs = entry.getValue().getTags().get(NODEREF).toString().split(";");
                LinkedList<OSMNode> nodes = new LinkedList<>();
                for (String ref : refs) {
                    if (map.containsKey(ref))
                        nodes.add((OSMNode) map.get(ref));
                    //TODO: handle missing nodes
                }
                if (nodes.size() < 2)
                    continue; // cannot create way, if there are less than 2 nodes
                map.put(entry.getValue().getIdentifier().toString(), new OSMWay(entry.getValue(), null, nodes, null));
            }
        }
        //return
        return new OSMFeatureCollection<>(identifier, map.values(), null);
    }

    private Graph<OSMRelation> initOSMRelations(IIdentifier identifier, Map<String,OSMPropertySet> featureMap, OSMFeatureCollection<OSMVectorFeature> features) {
        Collection<OSMRelation> collection = new HashSet<>();
	    for (Map.Entry<String,OSMPropertySet> entry : featureMap.entrySet()) {
            if (entry.getValue().getTags() != null && entry.getValue().getTags().containsKey(RELATION_MEMBER)) {
				IIdentifier relationId = entry.getValue().getIdentifier();
                String[] members = entry.getValue().getTags().get(RELATION_MEMBER).toString().split(";");
                Map<IRole,Set<OSMVectorFeature>> relationMembers = new HashMap<>();
                for (String member : members) {
                    String[] refRole = member.split(",", -1);
					IIdentifier featureId = new Identifier(refRole[0]);
                    if (features.getFeatureById(featureId) != null) {
                        IRole role = new Role(new Identifier(refRole[2]));
                        if (relationMembers.containsKey(role))
                            relationMembers.get(role).add(features.getFeatureById(featureId));
                        else
                            relationMembers.put(role, Sets.newHashSet(features.getFeatureById(featureId)));
                    }
                    //TODO: handle missing members
                }
                if (relationMembers.size() < 2)
                    continue; // cannot create relation, if there are less than 2 members
                collection.add(new OSMRelation(relationId, relationMembers, initRelationType(relationMembers.keySet()),null));
            }
        }
        //return
        return new Graph<>(identifier, collection, null);
    }

    private RelationType initRelationType(Set<IRole> roles) {
	    return new RelationType(null, roles, null);
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_RESOURCE, new InputConnector(
                IN_RESOURCE,
                new MetadataForConnector(IN_RESOURCE.toString(), "Link to input OSM XML"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_FEATURES, new OutputConnector(
                OUT_FEATURES,
                new MetadataForConnector(OUT_FEATURES.toString(), "Output OSM features"),
                new IDataConstraint[]{
                        new BindingConstraint(OSMFeatureCollection.class),
                        new MandatoryConstraint()},
                null));
        outputConnectors.put(OUT_RELATIONS, new OutputConnector(
                OUT_RELATIONS,
                new MetadataForConnector(OUT_RELATIONS.toString(), "Output OSM feature relations"),
                new IDataConstraint[]{
                        new BindingConstraint(Graph.class)},
                null));
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
