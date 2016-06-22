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

public class GTFeatureCollection extends AbstractDataResource implements IDataCollection<GTFeature> {

	private transient Map<String,GTFeature> featureMap;
	
	public GTFeatureCollection(String identifier, Collection<GTFeature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
	}
	
	public GTFeatureCollection(String identifier, Collection<GTFeature> featureCollection){
		this(identifier, featureCollection, null);
	}
	
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		this(identifier, getGTCollection(featureCollection), description);
	}
	
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(identifier, getGTCollection(featureCollection), null);
	}
	
	public static Collection<GTFeature> getGTCollection(FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection) {
		Collection<GTFeature> collection = new HashSet<GTFeature>();
		try (FeatureIterator<? extends Feature> iterator = featureCollection.features()){
		     while(iterator.hasNext()){
		    	 collection.add(new GTFeature(iterator.next()));
		     }
		}
		return collection;
	}

	public GTFeatureCollection(String identifier, InputStream xmlIS, Configuration configuration) throws IOException, XMLStreamException, SAXException {	
		super(identifier);
		featureMap = new HashMap<String,GTFeature>();		
		PullParser gmlParser = new PullParser(configuration, xmlIS, SimpleFeature.class);
		SimpleFeature feature = null;
	    while((feature = (SimpleFeature) gmlParser.parse()) != null) {        	
	    	String featureID = identifier == null ? feature.getID() : (identifier + "#" + feature.getID());
        	featureMap.put(featureID, new GTFeature(featureID, feature));
	    }
	    this.setObject(featureMap.values());
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
			featureMap.put(feature.identifier(), feature);
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
			featureMap.put(feature.identifier(), feature);
	}

}
