package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.LinkedList;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.literal.LiteralUtility;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFPredicateObject;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class RelationMeasurement<T extends Comparable<T>> extends RDFResource implements IRelationMeasurement<T>,IRDFTripleSet {

	private IRDFIdentifiableResource source, target;
	private IMeasurementDescription description;
	private T value;
	private transient Collection<IRDFPredicateObject> objectSet;
	
	public RelationMeasurement(IRI identifier, IRDFIdentifiableResource source, IRDFIdentifiableResource target, T value, IMeasurementDescription description) {
		super(identifier);
		this.source = source;
		this.target = target;
		this.value = value;
		this.description = description;
	}
	
	@Override
	public IRDFIdentifiableResource source() {
		return source;
	}

	@Override
	public IRDFIdentifiableResource target() {
		return target;
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int compareTo(T o) {
		return value().compareTo(o);
	}

	@Override
	public IMeasurementDescription description() {
		return description;
	}

	@Override
	public IRDFResource subject() {
		return this;
	}

	@Override
	public Collection<IRDFPredicateObject> objectSet() {
		if(objectSet != null)
			return objectSet;
		
		Collection<IRDFPredicateObject> objectSet = new LinkedList<IRDFPredicateObject>();
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_SOURCE.resource(), source()));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_RELATION_TARGET.resource(), target()));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_VALUE.resource(), LiteralUtility.literal(value)));
		objectSet.add(new RDFPredicateObject(RDFVocabulary.PREDICATE_DESCRIPTION.resource(), description()));
		return objectSet;
	}

}
