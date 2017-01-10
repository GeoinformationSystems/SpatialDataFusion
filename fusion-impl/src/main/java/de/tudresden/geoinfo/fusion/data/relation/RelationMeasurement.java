package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMeasurementData;
import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Description;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurement;

/**
 * relation measurement implementation
 */
public class RelationMeasurement<D extends IResource, R extends IResource> extends Subject implements IRelationMeasurement {
	
	//predicates
	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
	private static IResource DOMAIN = Predicates.HAS_DOMAIN.getResource();
	private static IResource RANGE = Predicates.HAS_RANGE.getResource();
    private static IResource MEASUREMENT = Objects.RELATION_MEASUREMENT.getResource();
	private static IResource VALUE = Predicates.VALUE.getResource();
	private static IResource OPERATION = Description.OPERATION.getResource();
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param value measurement value
	 */
	public RelationMeasurement(IIdentifier identifier, D domain, R range, IMeasurementData value) {
		super(identifier, value, value.getMetadata());
		//set domain, range and resource type
        put(DOMAIN, domain);
        put(RANGE, range);
		put(PREDICATE_TYPE, MEASUREMENT);
		//set objects
		put(VALUE, value);
		put(OPERATION, value.getMetadata().getMeasurementOperation());
	}

    @SuppressWarnings("unchecked")
    @Override
    public D getDomain() {
        return (D) getObject(DOMAIN);
    }

    @SuppressWarnings("unchecked")
    @Override
    public R getRange() {
        return (R) getObject(RANGE);
    }

	@Override
	public IMeasurementData resolve() {
		return (IMeasurementData) super.resolve();
	}

	@Override
	public IMetadataForMeasurement getMetadata() {
		return resolve().getMetadata();
	}

	@Override
	public IMeasurementData getMeasurement() {
		return resolve();
	}
}
