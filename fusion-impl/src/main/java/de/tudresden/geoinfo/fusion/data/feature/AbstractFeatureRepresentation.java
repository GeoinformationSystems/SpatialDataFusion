package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

/**
 * feature representation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractFeatureRepresentation extends Subject implements IFeatureRepresentation {

	private IFeatureType type;
	private IFeatureEntity entity;
	
	/**
	 * constructor
	 * @param identifier feature identifier
	 * @param representation feature representation
	 * @param description feature description
	 */
	public AbstractFeatureRepresentation(IIdentifier identifier, Object representation, IMetadataForData description){
		super(identifier, representation, description);
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
	public void setRelatedType(IFeatureType type){
		this.type = type;
	}
	
	/**
	 * set feature entity
	 * @param entity associated entity
	 */
	public void setRelatedEntity(IFeatureEntity entity){
		this.entity = entity;
	}

}
