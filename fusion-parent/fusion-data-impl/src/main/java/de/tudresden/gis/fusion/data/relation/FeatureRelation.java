package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class FeatureRelation<T extends IFeatureView> extends RDFResource implements IRelation<T>,IRDFTripleSet {
	
	private T source;
	private T target;
	private Collection<IRelationType> types;
	private Collection<IRelationMeasurement<?>> measurements;

	public FeatureRelation(IRI identifier, T source, T target, Collection<IRelationType> types, Collection<IRelationMeasurement<?>> measurements){
		super(identifier);
		this.source = source;
		this.target = target;
		this.types = types;
		this.measurements = measurements;
	}
	
	public FeatureRelation(IRI identifier, T source, T target){
		this(identifier, source, target, new HashSet<IRelationType>(), new HashSet<IRelationMeasurement<?>>());
	}
	
	public FeatureRelation(T source, T target){
		this(null, source, target);
	}
	
	@Override
	public Object value() {
		return this;
	}

	@Override
	public IDataDescription description() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public T source() {
		return source;
	}

	@Override
	public T target() {
		return target;
	}
	
	@Override
	public Collection<IRelationType> relationTypes() {
		return types;
	}

	@Override
	public Collection<IRelationMeasurement<?>> relationMeasurements() {
		return measurements;
	}
	
	@Override
	public IRDFResource subject() {
		return this;
	}

	@Override
	public Collection<IRDFPredicateObject> objectSet() {
		//init set
		Collection<IRDFPredicateObject> objectSet = new LinkedList<IRDFPredicateObject>();
		//add relation instance
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_TYPE.resource(), RDFVocabulary.TYPE_RELATION_FEATURE.resource()));
		//add feature view members
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_SOURCE.resource(), source));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_TARGET.resource(), target));
		//add relation types
		for(IRelationType type : relationTypes()){
			objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_TYPE.resource(), type));
		}
		//add measurements
		for(IRelationMeasurement<?> measurement : relationMeasurements()){
			objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_MEASUREMENT.resource(), measurement));
		}

		return objectSet;
	}
	
	@Override
	public void add(IRelationType type){
		types.add(type);
	}
	
	@Override
	public void add(IRelationMeasurement<?> measurement){
		measurements.add(measurement);
	}

}
