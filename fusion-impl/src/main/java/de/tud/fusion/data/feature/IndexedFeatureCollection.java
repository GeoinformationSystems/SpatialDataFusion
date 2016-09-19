package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.tud.fusion.data.description.IDataDescription;

/**
 * GeoTools indexed feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IndexedFeatureCollection<T extends AbstractFeature> extends FeatureCollection<T> {

	/**
	 * geometry index
	 */
	private STRtree index;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools GTFeature collection
	 * @param description collection description
	 */
	public IndexedFeatureCollection(String identifier, Collection<T> featureCollection, IDataDescription description){
		super(identifier, featureCollection, description);
		buildIndex();
	}
	
	/**
	 * build spatial index for feature collection
	 */
	private void buildIndex(){
		this.index = new STRtree();
		for(T feature : resolve()){
			addFeatureToIndex(feature);
		}
	}
	
	/**
	 * adds feature to spatial index
	 * @param feature input feature
	 */
	private void addFeatureToIndex(T feature){
		index.insert(ReferencedEnvelope.reference(((Feature) feature.getRepresentation().resolve()).getBounds()), feature);
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
