package de.tudresden.gis.fusion.data.feature.geotools;

import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.tudresden.gis.fusion.data.description.IDataDescription;

public class GTIndexedFeatureCollection extends GTFeatureCollection {

	private STRtree index;
	
	public GTIndexedFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
		buildIndex();
	}
	
	public GTIndexedFeatureCollection(String identifier, FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		super(identifier, featureCollection);
	}
	
	public GTIndexedFeatureCollection(FeatureCollection<? extends FeatureType,? extends Feature> featureCollection){
		super(featureCollection);
	}
	
	/**
	 * build spatial index for feature collection
	 */
	private void buildIndex(){
		this.index = new STRtree();
		try (FeatureIterator<? extends Feature> iterator = super.collection().features() ){
		     while(iterator.hasNext()){
		          addFeatureToIndex(iterator.next());
		     }
		 }
	}
	
	/**
	 * adds feature to spatial index
	 * @param feature input feature
	 */
	private void addFeatureToIndex(Feature feature){
		index.insert(ReferencedEnvelope.reference(feature.getBounds()), feature);
	}
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @return all intersecting features
	 */
	@SuppressWarnings("unchecked")
	public List<Feature> boundsIntersect(Feature feature){
		return this.index.query(ReferencedEnvelope.reference(feature.getBounds()));
	}
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @param buffer buffer tolerance applied to bounds
	 * @return all intersecting features within tolerance
	 */
	@SuppressWarnings("unchecked")
	public List<Feature> boundsIntersect(Feature feature, double buffer){
		Envelope envelope = ReferencedEnvelope.reference(feature.getBounds());
		envelope.expandBy(buffer);
		return this.index.query(envelope);
	}

}
