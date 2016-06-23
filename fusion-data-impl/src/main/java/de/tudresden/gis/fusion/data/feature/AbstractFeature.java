package de.tudresden.gis.fusion.data.feature;

import java.util.HashSet;
import java.util.Set;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;

/**
 * abstract feature implementation
 * @author Stefan Wiemann, TU Dresden
 *
 * @param <T> feature object type
 */
public abstract class AbstractFeature<T extends Object> extends AbstractDataResource implements IFeature {

	/**
	 * feature concept
	 */
	private IFeatureConcept concept;
	
	/**
	 * feature type
	 */
	private IFeatureType type;
	
	/**
	 * feature entity
	 */
	private IFeatureEntity entity;
	
	/**
	 * feature representation
	 */
	private IFeatureRepresentation representation;
	
	/**
	 * relations associated with the feature
	 */
	private Set<IFeatureRelation> relations;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature feature object
	 * @param description feature description
	 */
	public AbstractFeature(String identifier, T feature, IDataDescription description){
		super(identifier, feature, description);
		initFeature(feature);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param feature feature object
	 */
	public AbstractFeature(String identifier, T feature){
		this(identifier, feature, null);
	}
	
	/**
	 * constructor
	 * @param feature feature object
	 */
	public AbstractFeature(T feature){
		this(null, feature, null);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public AbstractFeature(String identifier){
		super(identifier);
	}

	@Override
	public IFeatureConcept getConcept() {
		return concept;
	}

	@Override
	public IFeatureType getType() {
		return type;
	}

	@Override
	public IFeatureEntity getEntity() {
		return entity;
	}

	@Override
	public IFeatureRepresentation getRepresentation() {
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
	 * initialize feature
	 */
	private void initFeature(T feature){
		this.concept = initConcept(feature);
		this.type = initType(feature);
		this.entity = initEntity(feature);
		this.representation = initRepresentation(feature);
	}
	
	/**
	 * initialize concept
	 * @return concept
	 */
	public abstract IFeatureConcept initConcept(T feature);
	
	/**
	 * initialize type
	 * @return type
	 */
	public abstract IFeatureType initType(T feature);
	
	/**
	 * initialize entity
	 * @return entity
	 */
	public abstract IFeatureEntity initEntity(T feature);
	
	/**
	 * initialize representation
	 * @return representation
	 */
	public abstract IFeatureRepresentation initRepresentation(T feature);

}
