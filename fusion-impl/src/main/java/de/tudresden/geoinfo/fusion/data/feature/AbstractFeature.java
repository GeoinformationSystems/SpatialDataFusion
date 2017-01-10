package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.data.relation.IRelation;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFeature extends Subject implements IFeature {
	
	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
	private static IResource TYPE_FEATURE = Objects.FEATURE.getResource();
	
	private AbstractFeatureConcept concept;
	private AbstractFeatureType type;
	private AbstractFeatureEntity entity;
	private AbstractFeatureRepresentation representation;
	private Set<IRelation<? extends IFeature>> relations;

	/**
	 * constructor
	 * @param identifier feature identifier
	 * @param feature feature object
	 * @param description feature description
	 * @param relations feature relations
	 */
	public AbstractFeature(IIdentifier identifier, Object feature, IMetadataForData description, Set<IRelation<? extends IFeature>> relations) {
        super(identifier, feature, description);
        this.relations = relations;
        //set resource type
        put(PREDICATE_TYPE, TYPE_FEATURE);
    }

	@Override
	public AbstractFeatureConcept getConcept() {
		if(concept == null)
			concept = initConcept();
		return concept;
	}

	@Override
	public AbstractFeatureType getType() {
		if(type == null)
			type = initType();
		return type;
	}

	@Override
	public AbstractFeatureEntity getEntity() {
		if(entity == null)
			entity = initEntity();
		return entity;
	}

	@Override
	public AbstractFeatureRepresentation getRepresentation() {
		if(representation == null)
			representation = initRepresentation();
		return representation;
	}

	@Override
	public Set<IRelation<? extends IFeature>> getRelations() {
		return relations;
	}
	
	/**
	 * add a relation to this feature
	 * @param relation input feature relation
	 */
	public void addRelation(IRelation<? extends IFeature> relation){
		if(relations == null)
			relations = new HashSet<>();
		relations.add(relation);
	}
	
	/**
	 * initialize feature concept
	 * @return feature concept
	 */
	public abstract AbstractFeatureConcept initConcept();
	
	/**
	 * initialize feature type
	 * @return feature type
	 */
	public abstract AbstractFeatureType initType();
	
	/**
	 * initialize feature entity
	 * @return feature entity
	 */
	public abstract AbstractFeatureEntity initEntity();
	
	/**
	 * initialize feature representation
	 * @return feature representation
	 */
	public abstract AbstractFeatureRepresentation initRepresentation();

}
