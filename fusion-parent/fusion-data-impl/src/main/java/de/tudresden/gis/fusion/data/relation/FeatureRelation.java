package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class FeatureRelation extends AbstractDataResource implements IFeatureRelation,ITripleSet {
	
	private ObjectSet objectSet;
	
	//predicates
	private IIdentifiableResource RESOURCE_TYPE = RDFVocabulary.TYPE.asResource();
	private IIdentifiableResource SOURCE = RDFVocabulary.RELATION_SOURCE.asResource();
	private IIdentifiableResource TARGET = RDFVocabulary.RELATION_TARGET.asResource();
	private IIdentifiableResource RELATION_TYPE = RDFVocabulary.RELATION_TYPE.asResource();
	private IIdentifiableResource VIEW = RDFVocabulary.RELATION_VIEW.asResource();
	private IIdentifiableResource MEASUREMENTS = RDFVocabulary.RELATION_MEASUREMENT.asResource();

	public FeatureRelation(String identifier, IFeature source, IFeature target, IIdentifiableResource view, IRelationType type, Collection<IRelationMeasurement> measurements){
		super(identifier);
		objectSet = new ObjectSet();
		//set resource type
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.FEATURE_RELATION.asResource());
		//set objects
		objectSet.put(SOURCE, source, true);
		objectSet.put(TARGET, target, true);
		objectSet.put(RELATION_TYPE, type);
		objectSet.put(VIEW, view);
		objectSet.put(MEASUREMENTS, measurements);
	}
	
	public FeatureRelation(String identifier, IFeature source, IFeature target){
		this(identifier, source, target, null, null, null);
	}
	
	public FeatureRelation(IFeature source, IFeature target){
		this(null, source, target);
	}
	
	@Override
	public IFeature getSource() {
		return (IFeature) objectSet.getFirst(SOURCE);
	}

	@Override
	public IFeature getTarget() {
		return (IFeature) objectSet.getFirst(TARGET);
	}
	
	@Override
	public IRelationType getRelationType() {
		return (IRelationType) objectSet.getFirst(RELATION_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IRelationMeasurement> getRelationMeasurements() {
		return (Collection<IRelationMeasurement>) objectSet.getFirst(MEASUREMENTS);
	}
	
	@Override
	public IIdentifiableResource getFeatureView() {
		return (IIdentifiableResource) objectSet.getFirst(VIEW);
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
	public int size() {
		return objectSet.numberOfObjects();
	}
	
	@Override
	public void addMeasurement(IRelationMeasurement measurement){
		objectSet.put(MEASUREMENTS, measurement);
	}
	
}
