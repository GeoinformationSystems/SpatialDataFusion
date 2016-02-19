package de.tudresden.gis.fusion.data.relation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.ISubjectCollection;
import de.tudresden.gis.fusion.data.rdf.ITripleSet;
import de.tudresden.gis.fusion.data.rdf.ObjectSet;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class FeatureRelationCollection extends Resource implements ITripleSet,ISubjectCollection,IDataCollection<IFeatureRelation> {

	int currentIndex = 1;
	private ObjectSet objectSet;
	private Collection<IFeatureRelation> relations;
	
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
		this.relations = relations;
		for(IFeatureRelation relation : relations){
			objectSet.put(getIndexForInsert(), relation);
		}
	}
	
	public FeatureRelationCollection(String identifier) {
		this(identifier, new HashSet<IFeatureRelation>(), null);
	}
	
	/**
	 * get current index for relation insert; index is increased by one
	 * @return current index (number of relations in collection)
	 */
	private IIdentifiableResource getIndexForInsert(){
		return new Resource(RDFVocabulary.RDF.asString() + "#_" + currentIndex++);
	}
	
	public FeatureRelationCollection() {
		this(null);
	}
	
	/**
	 * add relation to collection
	 * @param relation input feature relation
	 */
	public void add(IFeatureRelation relation){
		objectSet.put(getIndexForInsert(), relation);
		relations.add(relation);
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
	
}
