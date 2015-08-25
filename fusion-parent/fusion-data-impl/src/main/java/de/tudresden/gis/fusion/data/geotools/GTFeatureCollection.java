package de.tudresden.gis.fusion.data.geotools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.AFeatureRepresentation;

public class GTFeatureCollection extends AFeatureRepresentation implements IDataCollection<GTFeature> {

	private transient Map<IRI,GTFeature> featureMap;
	
	public GTFeatureCollection(IRI identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
	}
	
	public GTFeatureCollection(FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		this(new IRI(featureCollection.getID()), featureCollection, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FeatureCollection<? extends FeatureType,? extends Feature> value(){
		return (FeatureCollection<? extends FeatureType,? extends Feature>) super.value();
	}
	
	@Override
	public Iterator<GTFeature> iterator() {
		return collection().iterator();
	}

	@Override
	public int size() {
		return this.value().size();
	}

	@Override
	public Collection<GTFeature> collection() {
		if(featureMap == null)
			initMap();
		return featureMap.values();
	}

	@Override
	public GTFeature elementById(IRI identifier) {
		if(featureMap == null)
			initMap();
		return featureMap.get(identifier);
	}

	/**
	 * initialize feature map
	 */
	private void initMap() {
		featureMap = new HashMap<IRI,GTFeature>();
		try (FeatureIterator<? extends Feature> iterator = this.value().features()){
		     while(iterator.hasNext()){
		           GTFeature feature = new GTFeature(iterator.next());
		           featureMap.put(feature.identifier(), feature);
		     }
		}
	}

	@Override
	public void add(GTFeature object) {
		throw new UnsupportedOperationException();
	}

}
