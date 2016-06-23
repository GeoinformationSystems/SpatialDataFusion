package de.tudresden.gis.fusion.data.feature;

import de.tudresden.gis.fusion.data.AbstractDataResource;

/**
 * feature representation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureRepresentation extends AbstractDataResource implements IFeatureRepresentation {

	/**
	 * type associated with this representation
	 */
	private IFeatureType type;
	
	/**
	 * entity represented
	 */
	private IFeatureEntity entity;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param representation representation object
	 */
	public FeatureRepresentation(String identifier, Object representation){
		super(identifier, representation);
	}
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 */
	public FeatureRepresentation(String identifier){
		super(identifier, null);
	}
	
	/**
	 * constructor
	 * @param representation representation object
	 */
	public FeatureRepresentation(Object representation){
		this(null, representation);
	}

	@Override
	public IFeatureType getRelatedType() {
		return type;
	}

	@Override
	public IFeatureEntity getRelatedEntity() {
		return entity;
	}
	
	/**
	 * set feature type
	 * @param type associated type
	 */
	public void setType(IFeatureType type){
		this.type = type;
	}
	
	/**
	 * set feature entity
	 * @param entity associated entity
	 */
	public void setEntity(IFeatureEntity entity){
		this.entity = entity;
	}

}
