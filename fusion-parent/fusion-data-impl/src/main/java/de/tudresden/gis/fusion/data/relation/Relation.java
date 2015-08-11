package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public abstract class Relation extends RDFResource implements IRelation,IRDFTripleSet,IRDFResource {
	
	private IFeatureView source;
	private IFeatureView target;
	private Collection<IRelationType> types;
	private Collection<IRelationMeasurement<?>> measurements;

	public Relation(IRI identifier, IFeatureView source, IFeatureView target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements){
		super(identifier);
		this.source = source;
		this.target = target;
		this.types = types;
		this.measurements = measurements;
	}
	
	public Relation(IRI identifier, IFeatureView source, IFeatureView target){
		this(identifier, source, target, null, null);
	}
	
	@Override
	public IFeatureView getSource() {
		return source;
	}

	@Override
	public IFeatureView getTarget() {
		return target;
	}
	
	@Override
	public Collection<IRelationType> getRelationTypes() {
		return types;
	}

	@Override
	public Collection<IRelationMeasurement<?>> getRelationMeasurements() {
		return measurements;
	}
	
	@Override
	public IRDFResource getSubject() {
		return this;
	}

	@Override
	public Collection<IRDFPredicateObject> getObjectSet() {
		//init set
		Collection<IRDFPredicateObject> objectSet = new HashSet<IRDFPredicateObject>();
		//add relation instance
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_TYPE.resource(), getRelationInstance()));
		//add feature view members
		objectSet.add(new RDFPredicateObject(RDFVocabulary.RELATION_SOURCE.resource(), source));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.RELATION_TARGET.resource(), target));
		//add relation types
		for(IRelationType type : getRelationTypes()){
			objectSet.add(new RDFPredicateObject(RDFVocabulary.RELATION_TYPE.resource(), type));
		}
		//add measurements
		for(IRelationMeasurement<?> measurement : getRelationMeasurements()){
			objectSet.add(new RDFPredicateObject(RDFVocabulary.RELATION_MEASUREMENT.resource(), measurement));
		}

		return objectSet;
	}
	
	/**
	 * adds relation type to this relation
	 * @param type relation type
	 */
	public void addRelationType(IRelationType type){
		if(types == null)
			types = new HashSet<IRelationType>();
		types.add(type);
	}
	
	/**
	 * adds relation measurement to this relation
	 * @param measurement relation measurement
	 */
	public void addRelationMeasurement(IRelationMeasurement<?> measurement){
		if(measurements == null)
			measurements = new HashSet<IRelationMeasurement<?>>();
		measurements.add(measurement);
	}
	
	/**
	 * get relation instance type
	 * @return relation instance type
	 */
	public abstract IRDFIdentifiableResource getRelationInstance();

}
