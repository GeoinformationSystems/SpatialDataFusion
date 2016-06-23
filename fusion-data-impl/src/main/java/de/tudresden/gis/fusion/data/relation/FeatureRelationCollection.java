package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.rdf.IGraph;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.Subject;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

/**
 * collection of feature relations
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureRelationCollection extends AbstractDataResource implements ISubject,IGraph,IDataCollection<IFeatureRelation> {

	/**
	 * index count (for RDF encoding)
	 */
	int currentIndex = 1;
	
	/**
	 * relation subject
	 */
	private Subject subject;
	
	/**
	 * relation collection with index
	 */
	private HashMap<String,Collection<IFeatureRelation>> relationIndex;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param relations input relations
	 * @param description relation collection description
	 */
	public FeatureRelationCollection(String identifier, Collection<IFeatureRelation> relations, IDataDescription description) {
		super(identifier, relations, description);
		subject = new Subject(identifier);
		//set objects
		subject.put(RDFVocabulary.TYPE.getResource(), RDFVocabulary.BAG.getResource());
		subject.put(RDFVocabulary.DC_DESCRIPTION.getResource(), description);
		//insert member objects
		initIndex();
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public FeatureRelationCollection(String identifier) {
		this(identifier, new HashSet<IFeatureRelation>(), null);
	}
	
	/**
	 * empty constructor
	 */
	public FeatureRelationCollection() {
		this(null);
	}
	
	/**
	 * add RDF member object
	 */
	private void addMember(IFeatureRelation relation) {
		subject.put(RDFVocabulary.MEMBER.getResource(), relation);
	}
	
	/**
	 * add relation to index
	 * @param relation input relation
	 */
	private void addToIndex(IFeatureRelation relation){
		//add to collection
		this.resolve().add(relation);
		//add source to index
		addToIndex(relation.getReference().getIdentifier(), relation);
		//add target to index
		addToIndex(relation.getTarget().getIdentifier(), relation);
	}
	
	/**
	 * add relation to key
	 * @param key relation key
	 * @param relation relation to associate with key 
	 */
	private void addToIndex(String key, IFeatureRelation relation) {
		//add to key
		if(relationIndex.containsKey(key))
			relationIndex.get(key).add(relation);
		//create new key
		else {
			Collection<IFeatureRelation> relations = new HashSet<IFeatureRelation>();
			relations.add(relation);
			relationIndex.put(key, relations);
		}
	}
	
	/**
	 * initialize collection index
	 */
	public void initIndex(){
		relationIndex = new HashMap<String,Collection<IFeatureRelation>>();
		addAll(resolve());
	}
	
	/**
	 * add relation to index
	 * @param relation input relation
	 */
	public void add(IFeatureRelation relation){
		//add to member objects
		addMember(relation);
		//add to index
		addToIndex(relation);
	}
	
	/**
	 * add relation collection to index
	 * @param relations input relations
	 */
	public void addAll(Collection<IFeatureRelation> relations){
		for(IFeatureRelation relation : relations){
			add(relation);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<IFeatureRelation> resolve(){
		return (Collection<IFeatureRelation>) super.resolve();
	}

	@Override
	public Iterator<IFeatureRelation> iterator() {
		return resolve().iterator();
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
	public Collection<? extends ISubject> getSubjects() {
		Collection<ISubject> subjects = new HashSet<ISubject>();
		for(IFeatureRelation relation : resolve()){
			if(relation instanceof ISubject)
				subjects.add((ISubject) relation);
		}
		return subjects;
	}
	
	public Collection<IFeature> getSourceFeatures(){
		return getFeatures(true);
	}
	
	public Collection<IFeature> getTargetFeatures(){
		return getFeatures(false);
	}
	
	private Collection<IFeature> getFeatures(boolean source) {
		Map<String,IFeature> features = new HashMap<String,IFeature>();
		for(IFeatureRelation relation : resolve()){
			IFeature feature = source ? relation.getReference() : relation.getTarget();
			if(!features.containsKey(feature.getIdentifier()))
				features.put(feature.getIdentifier(), feature);
		}
		return features.values();
	}
	
	/**
	 * get relations associated with feature
	 * @param feature input feature
	 * @return relations associated with input feature
	 */
	public Collection<IFeatureRelation> getRelations(IFeature feature) {
		return relationIndex.get(feature.getIdentifier());
	}
	
}
