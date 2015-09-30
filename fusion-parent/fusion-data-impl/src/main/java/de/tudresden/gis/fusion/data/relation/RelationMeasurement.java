package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class RelationMeasurement extends Resource implements IRelationMeasurement,ITripleSet {

	private ObjectSet objectSet;
	
	//predicates
	private IIdentifiableResource RESOURCE_TYPE = RDFVocabulary.TYPE.asResource();
	private IIdentifiableResource SOURCE = RDFVocabulary.RELATION_SOURCE.asResource();
	private IIdentifiableResource TARGET = RDFVocabulary.RELATION_TARGET.asResource();
	private IIdentifiableResource VALUE = RDFVocabulary.VALUE.asResource();
	private IIdentifiableResource DESCRIPTION = RDFVocabulary.DESCRIPTION.asResource();
	
	public RelationMeasurement(String identifier, IIdentifiableResource source, IIdentifiableResource target, IMeasurement value, IMeasurementDescription description) {
		super(identifier);
		objectSet = new ObjectSet();
		//set resource type
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.RELATION_MEASUREMENT.asResource());
		//set objects
		objectSet.put(SOURCE, source, true);
		objectSet.put(TARGET, target, true);
		objectSet.put(VALUE, value);
		objectSet.put(DESCRIPTION, description);
	}
	
	public RelationMeasurement(IIdentifiableResource source, IIdentifiableResource target, IMeasurement value, IMeasurementDescription description) {
		this(null, source, target, value, description);
	}
	
	@Override
	public IIdentifiableResource getSource() {
		return (IIdentifiableResource) objectSet.getFirst(SOURCE);
	}

	@Override
	public IIdentifiableResource getTarget() {
		return (IIdentifiableResource) objectSet.getFirst(TARGET);
	}

	@Override
	public IMeasurement resolve() {
		return (IMeasurement) objectSet.get(VALUE);
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		return resolve().compareTo(measurement);
	}

	@Override
	public IMeasurementDescription getDescription() {
		return (IMeasurementDescription) objectSet.getFirst(DESCRIPTION);
	}

	@Override
	public Collection<IIdentifiableResource> getPredicates() {
		return objectSet.keySet();
	}

	@Override
	public Set<INode> getObject(IIdentifiableResource predicate) {
		return objectSet.get(predicate);
	}

	@Override
	public String getValue() {
		return resolve().getValue();
	}

	@Override
	public int size() {
		return objectSet.numberOfObjects();
	}

}
