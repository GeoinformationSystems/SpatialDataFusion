package de.tudresden.geoinfo.fusion.data.feature.geotools;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeature;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * GeoTools feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class GTFeatureCollection extends AbstractFeatureCollection<GTVectorFeature> {

    private ReferencedEnvelope envelope;
	
	/**
	 * constructor
     * @param identifier resource identifier
     * @param featureCollection GeoTools GTVectorFeature collection
     * @param description collection description
     */
	public GTFeatureCollection(IIdentifier identifier, Collection<GTVectorFeature> featureCollection, IMetadataForData description){
		super(identifier, featureCollection, description);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param featureCollection GeoTools feature collection
	 * @param description collection description
	 */
	public GTFeatureCollection(IIdentifier identifier, SimpleFeatureCollection featureCollection, IMetadataForData description){
		this(identifier, getGTCollection(identifier, featureCollection), description);
	}

	/**
	 * get GeoTools AbstractFeatureCollection
	 * @return GeoTools AbstractFeatureCollection
	 */
	public SimpleFeatureCollection collection() {
		List<SimpleFeature> featureList = new ArrayList<>();
		for(AbstractFeature feature : resolve()){
			featureList.add((SimpleFeature) feature.resolve());
		}
		return DataUtilities.collection(featureList);
	}

	@Override
	public Envelope getBounds() {
        for(GTVectorFeature feature : resolve()){
            if(envelope == null) {
                envelope = new ReferencedEnvelope((Envelope) feature.getRepresentation().getBounds());
                continue;
            }
            expandEnvelope(feature);
        }
        return envelope;
	}

	@Override
    public void add(GTVectorFeature feature) {
	    super.add(feature);
	    if(envelope != null)
            expandEnvelope(feature);
    }

    /**
     * expand envelope with feature envelope
     * @param feature input feature
     */
    private void expandEnvelope(GTVectorFeature feature){
	    this.envelope.expandToInclude(new ReferencedEnvelope((Envelope) feature.getRepresentation().getBounds()));
    }

	@Override
	public CoordinateReferenceSystem getReferenceSystem() {
		//return crs of first object in collection
		return (CoordinateReferenceSystem) resolve().iterator().next().getRepresentation().getReferenceSystem();
	}

    /**
     * create collection from GeoTools feature collection
     * @param featureCollection input collection
     * @return collection of GTVectorFeature implementations
     */
    public static Collection<GTVectorFeature> getGTCollection(IIdentifier collectionId, SimpleFeatureCollection featureCollection) {
        Collection<GTVectorFeature> collection = new HashSet<>();
        try (SimpleFeatureIterator iterator = featureCollection.features()){
            while(iterator.hasNext()){
                SimpleFeature feature = iterator.next();
                IIdentifier featureID = collectionId == null ? new Identifier(feature.getIdentifier().getID()) : new Identifier((collectionId + "#" + feature.getIdentifier().getID()));
                collection.add(new GTVectorFeature(featureID, feature, null, null));
            }
        }
        return collection;
    }

}
