package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;

public class FeatureConcept extends AbstractDataResource implements IFeatureConcept {
	
	private Collection<IFeatureEntity> entities;
	private Collection<IFeatureType> types;
	
	public FeatureConcept(String identifier, Object concept){
		super(identifier, concept);
	}
	
	public FeatureConcept(Object concept){
		this(null, concept);
	}
	
	@Override
	public Collection<IFeatureEntity> getRelatedEntities() {
		return entities;
	}
	
	@Override
	public Collection<IFeatureType> getRelatedTypes() {
		return types;
	}
	
	/**
	 * adds a feature type
	 * @param type input type
	 */
	public void addType(IFeatureType type){
		if(types == null)
			types = new HashSet<IFeatureType>();
		types.add(type);
	}
	
	/**
	 * adds a feature entity
	 * @param entity input entity
	 */
	public void addEntity(IFeatureEntity entity){
		if(entities == null)
			entities = new HashSet<IFeatureEntity>();
		entities.add(entity);
	}

}
