package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Set;

/**
 * feature relation implementation
 */
public class BinaryRelation<T extends IResource> extends Relation<T> implements IBinaryRelation<T> {

	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
	private static IResource TYPE_RELATION = Objects.BINARY_RELATION.getResource();
	private static IResource DOMAIN = Predicates.HAS_DOMAIN.getResource();
	private static IResource RANGE = Predicates.HAS_RANGE.getResource();
	private static IResource MEASUREMENT = Objects.RELATION_MEASUREMENT.getResource();

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param domain relation domain
	 * @param range relation range
	 * @param type relation types
	 * @param measurements relation measurements
	 */
	public BinaryRelation(IIdentifier identifier, T domain, T range, IBinaryRelationType type, IMetadataForData metadata, Set<IRelationMeasurement> measurements){
		super(identifier, null, type, metadata);
		setMembers(domain, range, type);
		put(PREDICATE_TYPE, TYPE_RELATION);
		put(DOMAIN, domain);
		put(RANGE, range);
		put(MEASUREMENT, measurements);
	}

	@SuppressWarnings("unchecked")
    @Override
	public T getDomain() {
		return (T) getObject(DOMAIN);
	}

    @SuppressWarnings("unchecked")
	@Override
	public T getRange() {
		return (T) getObject(RANGE);
	}

    @Override
	public IBinaryRelationType getRelationType() {
		return (IBinaryRelationType) super.getRelationType();
	}

	@SuppressWarnings("unchecked")
    @Override
	public Set<IRelationMeasurement> getMeasurements() {
		return (Set<IRelationMeasurement>) getObject(MEASUREMENT);
	}

    @Override
	public void addMeasurement(IRelationMeasurement measurement){
		put(MEASUREMENT, measurement);
	}

    @Override
	public void addMeasurements(Set<IRelationMeasurement> measurements){
		put(MEASUREMENT, measurements);
	}
	
}
