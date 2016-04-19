package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;
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
	private IIdentifiableResource MEASUREMENT = RDFVocabulary.RELATION_MEASUREMENT.asResource();

	public FeatureRelation(String identifier, IFeature source, IFeature target, Set<IRelationType> types, Set<IRelationMeasurement> measurements){
		super(identifier);
		objectSet = new ObjectSet();
		//set resource type
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.FEATURE_RELATION.asResource());
		//set objects
		objectSet.put(SOURCE, source, true);
		objectSet.put(TARGET, target, true);
		objectSet.put(RELATION_TYPE, types);
		objectSet.put(MEASUREMENT, measurements);
	}
	
	public FeatureRelation(String identifier, IFeature source, IFeature target){
		this(identifier, source, target, null, null);
	}
	
	public FeatureRelation(IFeature source, IFeature target){
		this(null, source, target);
	}
	
	@Override
	public IFeature getSource() {
		return (IFeature) objectSet.getSingle(SOURCE);
	}

	@Override
	public IFeature getTarget() {
		return (IFeature) objectSet.getSingle(TARGET);
	}
	
	@Override
	public Set<IRelationType> getRelationTypes() {
		Set<INode> objects = objectSet.get(RELATION_TYPE);
		Set<IRelationType> relationTypes = new HashSet<IRelationType>();
		for(INode object : objects){
			if(object instanceof IRelationType)
				relationTypes.add((IRelationType) object);
			else //should not happen
				throw new RuntimeException("node does not implement IRelationType");
		}
		return(relationTypes);
	}

	@Override
	public Set<IRelationMeasurement> getRelationMeasurements() {
		Set<INode> objects = objectSet.get(MEASUREMENT);
		Set<IRelationMeasurement> relationMeasurements = new HashSet<IRelationMeasurement>();
		for(INode object : objects){
			if(object instanceof IRelationMeasurement)
				relationMeasurements.add((IRelationMeasurement) object);
			else //should not happen
				throw new RuntimeException("node does not implement IRelationMeasurement");
		}
		return(relationMeasurements);
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
		objectSet.put(MEASUREMENT, measurement);
	}
	
}
