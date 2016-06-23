package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.AbstractDataResource;

/**
 * feature concept implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureConcept extends AbstractDataResource implements IFeatureConcept {
	
	/**
	 * entities defined by this concept
	 */
	private Collection<IFeatureEntity> entities;
	
	/**
	 * types associated with this concept
	 */
	private Collection<IFeatureType> types;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param concept feature concept
	 */
	public FeatureConcept(String identifier, Object concept){
		super(identifier, concept);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public FeatureConcept(String identifier){
		this(identifier, null);
	}
	
	/**
	 * constructor
	 * @param concept feature concept
	 */
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
	 * adds an associated feature type
	 * @param type associated type
	 */
	public void addType(IFeatureType type){
		if(types == null)
			types = new HashSet<IFeatureType>();
		types.add(type);
	}
	
	/**
	 * adds an associated feature entity
	 * @param entity associated entity
	 */
	public void addEntity(IFeatureEntity entity){
		if(entities == null)
			entities = new HashSet<IFeatureEntity>();
		entities.add(entity);
	}

}
