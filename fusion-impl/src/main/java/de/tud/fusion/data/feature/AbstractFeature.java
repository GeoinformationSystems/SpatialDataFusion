package de.tud.fusion.data.feature;

import java.util.HashSet;
import java.util.Set;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.RDFVocabulary;
import de.tud.fusion.data.rdf.Subject;
import de.tud.fusion.data.relation.IFeatureRelation;

public abstract class AbstractFeature extends Subject implements IFeature {
	
	private final IResource PREDICATE_TYPE = RDFVocabulary.TYPE.getResource();
	private final IResource TYPE_FEATURE = RDFVocabulary.FEATURE.getResource();
	
	private FeatureConceptView concept;
	private FeatureTypeView type;
	private FeatureEntityView entity;
	private FeatureRepresentationView representation;
	private Set<IFeatureRelation> relations;
	
	/**
	 * constructor
	 * @param concept feature concept
	 * @param type feature type
	 * @param entity feature entity
	 * @param representation feature representation
	 * @param relations feature relations
	 */
	public AbstractFeature(String identifier, Object feature, IDataDescription description, Set<IFeatureRelation> relations){
		super(identifier, feature, description);
		this.relations = relations;
		//set resource type
		put(PREDICATE_TYPE, TYPE_FEATURE);
	}

	@Override
	public FeatureConceptView getConcept() {
		if(concept == null)
			concept = initConcept();
		return concept;
	}

	@Override
	public FeatureTypeView getType() {
		if(type == null)
			type = initType();
		return type;
	}

	@Override
	public FeatureEntityView getEntity() {
		if(entity == null)
			entity = initEntity();
		return entity;
	}

	@Override
	public FeatureRepresentationView getRepresentation() {
		if(representation == null)
			representation = initRepresentation();
		return representation;
	}

	@Override
	public Set<IFeatureRelation> getRelations() {
		return relations;
	}
	
	/**
	 * add a relation to this feature
	 * @param relation input feature relation
	 */
	public void addRelation(IFeatureRelation relation){
		if(relations == null)
			relations = new HashSet<IFeatureRelation>();
		relations.add(relation);
	}
	
	/**
	 * initialize feature concept
	 * @return feature concept
	 */
	public abstract FeatureConceptView initConcept();
	
	/**
	 * initialize feature type
	 * @return feature type
	 */
	public abstract FeatureTypeView initType();
	
	/**
	 * initialize feature entity
	 * @return feature entity
	 */
	public abstract FeatureEntityView initEntity();
	
	/**
	 * initialize feature representation
	 * @return feature representation
	 */
	public abstract FeatureRepresentationView initRepresentation();

}
