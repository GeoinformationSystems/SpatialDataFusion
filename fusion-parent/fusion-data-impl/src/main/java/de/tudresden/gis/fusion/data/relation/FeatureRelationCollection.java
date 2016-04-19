package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.ISubjectCollection;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class FeatureRelationCollection extends Resource implements ITripleSet,ISubjectCollection,IDataCollection<IFeatureRelation> {

	int currentIndex = 1;
	private ObjectSet objectSet;
	private Collection<IFeatureRelation> relations;
	
	//indexes
	private HashMap<String,Collection<IFeatureRelation>> relationIndex;
	
	//predicates
	private IIdentifiableResource RESOURCE_TYPE = RDFVocabulary.TYPE.asResource();
	private IIdentifiableResource DESCRIPTION = RDFVocabulary.DC_DESCRIPTION.asResource();
	
	public FeatureRelationCollection(String identifier, Collection<IFeatureRelation> relations, IDataDescription description) {
		super(identifier);
		objectSet = new ObjectSet();
		//set objects
		objectSet.put(RESOURCE_TYPE, RDFVocabulary.BAG.asResource());
		objectSet.put(DESCRIPTION, description);
		//insert member objects
		for(IFeatureRelation relation : relations){
			this.add(relation);
		}
	}
	
	public FeatureRelationCollection(String identifier) {
		this(identifier, new HashSet<IFeatureRelation>(), null);
	}
	
	public FeatureRelationCollection() {
		this(null);
	}
	
	/**
	 * add relation to index
	 * @param relation input relation
	 */
	private void addToIndex(IFeatureRelation relation){
		
		if(relationIndex == null)
			relationIndex = new HashMap<String,Collection<IFeatureRelation>>();
		
		//add source
		addToIndex(relation.getSource().asString(), relation);
		
		//add target
		addToIndex(relation.getTarget().asString(), relation);
	}
	
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
	 * get current index for relation insert; index is increased by one
	 * @return current index (number of relations in collection)
	 */
	private IIdentifiableResource getIndexForInsert(){
		return new IdentifiableResource(RDFVocabulary.RDF.asString() + "#_" + currentIndex++);
	}
	
	/**
	 * add relation to collection
	 * @param relation input feature relation
	 */
	public void add(IFeatureRelation relation){
		if(relations == null)
			relations = new HashSet<IFeatureRelation>();
		objectSet.put(getIndexForInsert(), relation);
		relations.add(relation);
		addToIndex(relation);
	}
	
	public void addAll(IDataCollection<IFeatureRelation> relations){
		for(IFeatureRelation relation : relations){
			this.add(relation);
		}
	}

	@Override
	public Collection<IFeatureRelation> resolve() {
		return relations;
	}

	@Override
	public IDataDescription getDescription() {
		return (IDataDescription) objectSet.get(DESCRIPTION);
	}

	/**
	 * get collection size
	 * @return collection size
	 */
	public int size() {
		return objectSet.size();
	}

	@Override
	public Iterator<IFeatureRelation> iterator() {
		return resolve().iterator();
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
	public Collection<? extends ISubject> collection() {
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
		for(IFeatureRelation relation : this.relations){
			IFeature feature = source ? relation.getSource() : relation.getTarget();
			if(!features.containsKey(feature.asString()))
				features.put(feature.asString(), feature);
		}
		return features.values();
	}
	
	/**
	 * get relations associated with feature
	 * @param feature input feature
	 * @return relations associated with input feature
	 */
	public Collection<IFeatureRelation> getRelations(IFeature feature) {

		//get relations
		return relationIndex.get(feature.asString());
		
//		Collection<IFeatureRelation> relations = new HashSet<IFeatureRelation>();
//		for(IFeatureRelation relation : this.relations){
//			if(relation.getSource().asString() == feature.asString() || relation.getTarget().asString() == feature.asString())
//				relations.add(relation);
//		}
//		return relations;
	}
	
}
