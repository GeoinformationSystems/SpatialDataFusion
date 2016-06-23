package de.tudresden.gis.fusion.data.feature.geotools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.xml.Configuration;
import org.geotools.xml.PullParser;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.rdf.Subject;

/**
 * GeoTools feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeatureCollection extends AbstractDataResource implements IDataCollection<GTFeature>,ISubject {

	/**
	 * feature collection subject
	 */
	private Subject subject;
	
	/**
	 * map of GeoTools features
	 */
	private transient Map<String,GTFeature> featureMap;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools GTFeature collection
	 * @param description collection description
	 */
	public GTFeatureCollection(String identifier, Collection<GTFeature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
		initSubject(featureCollection);
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools GTFeature collection
	 */
	public GTFeatureCollection(String identifier, Collection<GTFeature> featureCollection){
		this(identifier, featureCollection, null);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools feature collection
	 * @param description collection description
	 */
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		this(identifier, getGTCollection(featureCollection), description);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools feature collection
	 */
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(identifier, getGTCollection(featureCollection), null);
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param xmlIS XML input stream with GML encoded features
	 * @param configuration parser configuration
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws SAXException
	 */
	public GTFeatureCollection(String identifier, InputStream xmlIS, Configuration configuration) throws IOException, XMLStreamException, SAXException {	
		super(identifier);
		featureMap = new HashMap<String,GTFeature>();		
		PullParser gmlParser = new PullParser(configuration, xmlIS, SimpleFeature.class);
		SimpleFeature feature = null;
	    while((feature = (SimpleFeature) gmlParser.parse()) != null) {        	
	    	String featureID = identifier == null ? feature.getID() : (identifier + "#" + feature.getID());
        	featureMap.put(featureID, new GTFeature(featureID, feature));
	    }
	    initSubject(featureMap.values());
	}
	
	/**
	 * initialize feature subject
	 * @param identifier 
	 * @param featureCollection
	 */
	private void initSubject(Collection<GTFeature> featureCollection) {
		subject = new Subject(this.getIdentifier());
		//set resource type
		subject.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.BAG.getResource());
		for(GTFeature feature : resolve()){
			subject.put(RDFVocabulary.MEMBER.getResource(), feature);
		}
	}
	
	/**
	 * create collection from GeoTools feature collection
	 * @param featureCollection input collection
	 * @return collection of GTFeature implementations
	 */
	public static Collection<GTFeature> getGTCollection(FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection) {
		Collection<GTFeature> collection = new HashSet<GTFeature>();
		try (FeatureIterator<? extends Feature> iterator = featureCollection.features()){
		     while(iterator.hasNext()){
		    	 collection.add(new GTFeature(iterator.next()));
		     }
		}
		return collection;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<GTFeature> resolve(){
		return (Collection<GTFeature>) super.resolve();
	}
	
	@Override
	public Iterator<GTFeature> iterator() {
		return this.resolve().iterator();
	}

	/**
	 * get size of collection
	 * @return number of features in collection
	 */
	public int size() {
		return resolve().size();
	}

	/**
	 * get GeoTools FeatureCollection
	 * @return GeoTools FeatureCollection
	 */
	public FeatureCollection<? extends FeatureType,? extends Feature> collection() {
		Collection<GTFeature> features = resolve();
		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
		for(GTFeature feature : features){
			featureList.add((SimpleFeature) feature.resolve());
		}
		return DataUtilities.collection(featureList);
	}

	/**
	 * get feature by id
	 * @param identifier feature id
	 * @return feature with specified id or null, if no such feature exists
	 */
	public GTFeature elementById(String identifier) {
		if(featureMap == null)
			initMap();
		return featureMap.get(identifier);
	}

	/**
	 * initialize feature map
	 */
	private void initMap() {
		featureMap = new HashMap<String,GTFeature>();
		for(GTFeature feature : resolve()){
			featureMap.put(feature.getIdentifier(), feature);
		}
	}

	/**
	 * add a feature
	 * @param feature input feature
	 */
	public void add(GTFeature feature) {
		this.resolve().add(feature);
		if(featureMap == null)
			initMap();
		else
			featureMap.put(feature.getIdentifier(), feature);
	}

	@Override
	public Set<IResource> getPredicates() {
		return subject.getPredicates();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) throws IllegalArgumentException {
		return subject.getObjects(predicate);
	}

}
