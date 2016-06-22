package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;
import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;

public abstract class AbstractFeature<T extends Object> extends AbstractDataResource implements IFeature {

	private IFeatureConcept concept;
	private IFeatureType type;
	private IFeatureEntity entity;
	private IFeatureRepresentation representation;
	private Collection<IFeatureRelation> relations;
	
	public AbstractFeature(String identifier, T feature, IDataDescription description){
		super(identifier, feature, description);
		initFeature(feature);
	}
	
	public AbstractFeature(String identifier, T feature){
		this(identifier, feature, null);
	}
	
	public AbstractFeature(T feature){
		this(null, feature, null);
	}
	
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
	public Collection<IFeatureRelation> getRelations() {
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
