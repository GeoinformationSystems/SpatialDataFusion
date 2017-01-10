package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.Graph;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.*;

/**
 * collection of feature relations
 */
public class FeatureRelationCollection extends Graph<IRelation<? extends IFeature>> {
	
	/**
	 * feature identifier with associated relations
	 */
	private HashMap<IIdentifier,Set<IRelation<? extends IFeature>>> relationIndex;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param relations input relations
	 * @param metadata relation collection metadata
	 */
	public FeatureRelationCollection(IIdentifier identifier, Collection<IRelation<? extends IFeature>> relations, IMetadataForData metadata) {
		super(identifier, relations, metadata);
		//insert member objects
		initIndex();
	}

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param metadata relation collection metadata
	 */
	public FeatureRelationCollection(IIdentifier identifier, IMetadataForData metadata) {
		this(identifier, new HashSet<>(), metadata);
	}
	
	/**
	 * add RDF member object
	 */
	private void addMember(IRelation<? extends IFeature> relation) {
		put(Predicates.MEMBER.getResource(), relation);
	}
	
	/**
	 * add relation to index
	 * @param relation input relation
	 */
	private void addToIndex(IRelation<? extends IFeature> relation){
		//add to collection
		this.resolve().add(relation);
		for(IFeature feature : relation.getMembers()){
            addToIndex(feature.getIdentifier(), relation);
        }
	}
	
	/**
	 * add relation to key
	 * @param key relation key
	 * @param relation relation to associate with key 
	 */
	private void addToIndex(IIdentifier key, IRelation<? extends IFeature> relation) {
		//add to key
		if(relationIndex.containsKey(key))
			relationIndex.get(key).add(relation);
		//create new key
		else {
			Set<IRelation<? extends IFeature>> relations = Sets.newHashSet(relation);
			relationIndex.put(key, relations);
		}
	}
	
	/**
	 * initialize collection index
	 */
	public void initIndex(){
		relationIndex = new HashMap<>();
		addAll(resolve());
	}
	
	/**
	 * add relation to index
	 * @param relation input relation
	 */
	public void add(IRelation<? extends IFeature> relation){
		//add to member collection
		addMember(relation);
		//add to index
		addToIndex(relation);
	}
	
	/**
	 * add relation collection to index
	 * @param relations input relations
	 */
	public void addAll(Collection<IRelation<? extends IFeature>> relations){
		for(IRelation<? extends IFeature> relation : relations){
			add(relation);
		}
	}

	@Override
	public Collection<IRelation<? extends IFeature>> resolve(){
		return super.resolve();
	}

	@Override
	public Iterator<IRelation<? extends IFeature>> iterator() {
		return resolve().iterator();
	}
	
	/**
	 * get all features with a particular role
	 * @return all features for the input role
	 */
	private Collection<IFeature> getFeatures(IRole role) {
        Set<IFeature> collection = new HashSet<>();
        for(IRelation<? extends IFeature> relation : resolve()){
            collection.addAll(relation.getMember(role));
        }
        return collection;
	}
	
	/**
	 * get relations associated with feature
	 * @param feature input feature
	 * @return relations associated with input feature
	 */
	public Set<IRelation<? extends IFeature>> getRelations(IFeature feature) {
		return relationIndex.get(feature.getIdentifier());
	}
	
}
