package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Graph;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * generic feature collection implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractFeatureCollection<T extends IFeature> extends Graph<T> implements IFeatureCollection<T> {
	
	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
	private static IResource PREDICATE_MEMBER = Predicates.MEMBER.getResource();
	private static IResource TYPE_BAG = Objects.BAG.getResource();
	
	/**
	 * map of features
	 */
	private transient Map<IIdentifier,T> featureMap;
	
	/**
     * constructor
     * @param identifier resource identifier
     * @param featureCollection Feature collection
     * @param metadata collection description
     */
    public AbstractFeatureCollection(IIdentifier identifier, Collection<T> featureCollection, IMetadataForData metadata){
        super(identifier, featureCollection, metadata);
        initSubject(featureCollection);
    }

	/**
	 * initialize feature subject
	 * @param featureCollection input feature collection
	 */
	private void initSubject(Collection<T> featureCollection) {
		//set resource type
		put(PREDICATE_TYPE, TYPE_BAG);
		for(IFeature feature : resolve()){
			put(PREDICATE_MEMBER, feature);
		}
	}

	/**
	 * get size of collection
	 * @return number of features in collection
	 */
	public int size() {
		return resolve().size();
	}

	/**
	 * get feature by id
	 * @param identifier feature id
	 * @return feature with specified id or null, if no such feature exists
	 */
	public T getFeatureById(IIdentifier identifier) {
		if(featureMap == null)
			initFeatureMap();
		return featureMap.get(identifier);
	}
	
	/**
	 * check if collection contains specified feature
	 * @param identifier feature identifier
	 * @return true, if collection contains feature with specified id
	 */
	public boolean containsId(IIdentifier identifier) {
		return featureMap.containsKey(identifier);
	}

	/**
	 * add a feature
	 * @param feature input feature
	 */
	public void add(T feature) {
		this.resolve().add(feature);
		if(featureMap == null)
			initFeatureMap();
		else {
			featureMap.put(feature.getIdentifier(), feature);
			put(PREDICATE_MEMBER, feature);
		}
	}
	
	/**
	 * initialize feature map
	 */
	private void initFeatureMap() {
		featureMap = new HashMap<>();
		for(T feature : resolve()){
			featureMap.put(feature.getIdentifier(), feature);
		}
	}

	@Override
	public abstract Envelope getBounds();

	@Override
	public abstract CoordinateReferenceSystem getReferenceSystem();

}
