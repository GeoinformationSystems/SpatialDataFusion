package de.tudresden.gis.fusion.data.feature;

import de.tudresden.gis.fusion.data.AbstractDataResource;

public class FeatureRepresentation extends AbstractDataResource implements IFeatureRepresentation {

	private IFeatureType type;
	private IFeatureEntity entity;
	
	public FeatureRepresentation(String identifier, Object representation){
		super(identifier, representation);
	}
	
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
	 * @param type input type
	 */
	public void setType(IFeatureType type){
		this.type = type;
	}
	
	/**
	 * set feature entity
	 * @param entity input entity
	 */
	public void setEntity(IFeatureEntity entity){
		this.entity = entity;
	}

}
