package de.tud.fusion.data.relation;

import java.util.Set;

import de.tud.fusion.data.feature.IFeature;
import de.tud.fusion.data.rdf.INode;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.rdf.Subject;

/**
 * feature relation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureRelation extends Subject implements IFeatureRelation {
	
	//predicates
	private IResource PREDICATE_TYPE = RDFVocabulary.TYPE.getResource();
	private IResource TYPE_RELATION = RDFVocabulary.FEATURE_RELATION.getResource();
	private IResource REFERENCE = RDFVocabulary.RELATION_REFERENCE.getResource();
	private IResource TARGET = RDFVocabulary.RELATION_TARGET.getResource();
	private IResource RELATION_TYPE = RDFVocabulary.RELATION_TYPE.getResource();
	private IResource MEASUREMENT = RDFVocabulary.RELATION_MEASUREMENT.getResource();

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param reference relation reference 
	 * @param target relation target
	 * @param types relation types
	 * @param measurements relation measurements
	 */
	public FeatureRelation(String identifier, IFeature reference, IFeature target, Set<IRelationType> types, Set<IRelationMeasurement> measurements){
		super(identifier, null, null);
		//set resource type
		put(PREDICATE_TYPE, TYPE_RELATION);
		//set objects
		put(REFERENCE, (INode) reference);
		put(TARGET, (INode) target);
		put(RELATION_TYPE, types);
		put(MEASUREMENT, measurements);
	}
	
	@Override
	public IFeature getReference() {
		return (IFeature) getObject(REFERENCE);
	}

	@Override
	public IFeature getTarget() {
		return (IFeature) getObject(TARGET);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<IRelationType> getRelationTypes() {
		return (Set<IRelationType>) getObject(RELATION_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<IRelationMeasurement> getRelationMeasurements() {
		return (Set<IRelationMeasurement>) getObject(MEASUREMENT);
	}
	
	/**
	 * add relation measurement
	 * @param measurement relatoin measurement
	 */
	public void addMeasurement(IRelationMeasurement measurement){
		put(MEASUREMENT, measurement);
	}
	
}
