package de.tudresden.gis.fusion.data.feature.geotools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;

public class GTFeatureCollection extends AbstractDataResource implements IDataCollection<GTFeature> {

	private transient Map<String,GTFeature> featureMap;
	
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
	}
	
	public GTFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(identifier, featureCollection, null);
	}
	
	public GTFeatureCollection(FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(featureCollection.getID(), featureCollection, null);
	}
	
	@Override
	public Collection<GTFeature> resolve(){
		if(featureMap == null)
			initMap();
		return featureMap.values();
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
	@SuppressWarnings("unchecked")
	public FeatureCollection<? extends FeatureType,? extends Feature> collection() {
		return (FeatureCollection<? extends FeatureType,? extends Feature>) super.resolve();
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
		try (FeatureIterator<? extends Feature> iterator = this.collection().features()){
		     while(iterator.hasNext()){
		           GTFeature feature = new GTFeature(iterator.next());
		           featureMap.put(feature.asString(), feature);
		     }
		}
	}

	/**
	 * add a feature
	 * @param object input feature
	 */
	public void add(GTFeature feature) {
		featureMap.put(feature.asString(), feature);
	}

}
