package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.Graph;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.*;

/**
 * collection of relation measurements
 */
public class RelationMeasurementCollection extends Graph<IRelationMeasurement> {

	/**
	 * feature identifier with associated relations
	 */
	private HashMap<IIdentifier,Set<IRelationMeasurement>> measurementIndex;

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param measurements input measurements
	 * @param metadata relation collection metadata
	 */
	public RelationMeasurementCollection(IIdentifier identifier, Collection<IRelationMeasurement> measurements, IMetadataForData metadata) {
		super(identifier, measurements, metadata);
		//insert member objects
		initIndex();
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param metadata relation collection metadata
	 */
	public RelationMeasurementCollection(IIdentifier identifier, IMetadataForData metadata) {
		this(identifier, new HashSet<>(), metadata);
	}
	
	/**
	 * add RDF member object
	 */
	private void addMember(IRelationMeasurement measurement) {
		put(Predicates.MEMBER.getResource(), measurement);
	}
	
	/**
	 * add relation to index
	 * @param measurement input relation
	 */
	private void addToIndex(IRelationMeasurement measurement){
		this.resolve().add(measurement);
        addToIndex(measurement.getDomain().getIdentifier(), measurement);
        addToIndex(measurement.getRange().getIdentifier(), measurement);
	}
	
	/**
	 * add relation to key
	 * @param key relation key
	 * @param measurement relation to associate with key
	 */
	private void addToIndex(IIdentifier key, IRelationMeasurement measurement) {
		//add to key
		if(measurementIndex.containsKey(key))
			measurementIndex.get(key).add(measurement);
		//create new key
		else {
            Set<IRelationMeasurement> measurements = Sets.newHashSet(measurement);
			measurementIndex.put(key, measurements);
		}
	}
	
	/**
	 * initialize collection index
	 */
	public void initIndex(){
		measurementIndex = new HashMap<>();
		addAll(resolve());
	}
	
	/**
	 * add relation to index
	 * @param measurement input relation
	 */
	public void add(IRelationMeasurement measurement){
		//add to member collection
		addMember(measurement);
		//add to index
		addToIndex(measurement);
	}
	
	/**
	 * add relation collection to index
	 * @param measurements input relations
	 */
	public void addAll(Collection<IRelationMeasurement> measurements){
		for(IRelationMeasurement measurement : measurements){
			add(measurement);
		}
	}

	@Override
	public Collection<IRelationMeasurement> resolve(){
		return super.resolve();
	}

	@Override
	public Iterator<IRelationMeasurement> iterator() {
		return resolve().iterator();
	}

	
	/**
	 * get measurements associated with a feature
	 * @param feature input feature
	 * @return measurements associated with input feature
	 */
	public Set<IRelationMeasurement> getMeasurements(IFeature feature) {
		return measurementIndex.get(feature.getIdentifier());
	}
	
}
