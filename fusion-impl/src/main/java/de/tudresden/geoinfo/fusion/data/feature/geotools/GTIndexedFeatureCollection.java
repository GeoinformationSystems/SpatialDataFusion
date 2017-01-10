package de.tudresden.geoinfo.fusion.data.feature.geotools;

import com.vividsolutions.jts.index.strtree.STRtree;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;

import java.util.Collection;
import java.util.List;

/**
 * GeoTools indexed feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTIndexedFeatureCollection extends GTFeatureCollection {

	/**
	 * geometry index
	 */
	private STRtree index;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection collection of GeoTools GTVectorFeature
	 * @param metadata collection description
	 */
	public GTIndexedFeatureCollection(IIdentifier identifier, Collection<GTVectorFeature> featureCollection, IMetadataForData metadata){
		super(identifier, featureCollection, metadata);
		buildIndex();
	}

	/**
	 * constructor
	 * @param featureCollection GeoTools GTVectorFeature collection
	 */
	public GTIndexedFeatureCollection(GTFeatureCollection featureCollection){
		this(featureCollection.getIdentifier(), featureCollection.resolve(), featureCollection.getMetadata());
	}
	
	/**
	 * build spatial index for feature collection
	 */
	private void buildIndex(){
		this.index = new STRtree();
		for(GTVectorFeature feature : resolve()){
			addFeatureToIndex(feature);
		}
	}
	
	/**
	 * adds feature to spatial index
	 * @param feature input feature
	 */
	private void addFeatureToIndex(GTVectorFeature feature){
		index.insert(new ReferencedEnvelope((Envelope) feature.getRepresentation().getBounds()), feature);
	}
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @return all intersecting features
	 */
	@SuppressWarnings("unchecked")
	public List<GTVectorFeature> boundsIntersect(Feature feature){
		return this.index.query(new ReferencedEnvelope((Envelope) feature.getBounds()));
	}

    /**
     * get intersecting features by input feature bounds
     * @param feature input feature
     * @return all intersecting features
     */
    public List<GTVectorFeature> boundsIntersect(GTVectorRepresentation feature){
        return boundsIntersect(feature.resolve());
    }
	
	/**
	 * get intersecting features by input feature bounds
	 * @param feature input feature
	 * @param buffer buffer tolerance applied to bounds
	 * @return all intersecting features within tolerance
	 */
	@SuppressWarnings("unchecked")
	public List<GTVectorFeature> boundsIntersect(Feature feature, double buffer){
		ReferencedEnvelope envelope = new ReferencedEnvelope((Envelope) feature.getBounds());
		envelope.expandBy(buffer);
		return this.index.query(envelope);
	}

    /**
     * get intersecting features by input feature bounds
     * @param feature input feature
     * @param buffer buffer tolerance applied to bounds
     * @return all intersecting features
     */
    public List<GTVectorFeature> boundsIntersect(GTVectorRepresentation feature, double buffer){
        return boundsIntersect(feature.resolve(), buffer);
    }
	
}
