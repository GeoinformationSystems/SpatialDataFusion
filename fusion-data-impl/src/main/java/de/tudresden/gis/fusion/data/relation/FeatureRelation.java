package de.tudresden.gis.fusion.data.relation;

import java.util.HashSet;
import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.feature.relation.IRelationType;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.Subject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * feature relation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureRelation extends AbstractDataResource implements IFeatureRelation,ISubject {
	
	/**
	 * relation subject
	 */
	private Subject subject;
	
	//predicates
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
		super(identifier);
		subject = new Subject(identifier);
		//set resource type
		subject.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.FEATURE_RELATION.getResource());
		//set objects
		subject.put(REFERENCE, reference, true);
		subject.put(TARGET, target, true);
		subject.put(RELATION_TYPE, types);
		subject.put(MEASUREMENT, measurements);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param reference relation reference 
	 * @param target relation target
	 */
	public FeatureRelation(String identifier, IFeature source, IFeature target){
		this(identifier, source, target, null, null);
	}
	
	/**
	 * constructor
	 * @param reference relation reference 
	 * @param target relation target
	 */
	public FeatureRelation(IFeature source, IFeature target){
		this(null, source, target);
	}
	
	@Override
	public IFeature getReference() {
		return (IFeature) subject.getSingle(REFERENCE);
	}

	@Override
	public IFeature getTarget() {
		return (IFeature) subject.getSingle(TARGET);
	}
	
	@Override
	public Set<IRelationType> getRelationTypes() {
		Set<INode> objects = subject.getObjects(RELATION_TYPE);
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
		Set<INode> objects = subject.getObjects(MEASUREMENT);
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
	public Set<IResource> getPredicates() {
		return subject.getPredicates();
	}

	@Override
	public Set<INode> getObjects(IResource predicate) {
		return subject.getObjects(predicate);
	}
	
	@Override
	public void addMeasurement(IRelationMeasurement measurement){
		subject.put(MEASUREMENT, measurement);
	}
	
}
