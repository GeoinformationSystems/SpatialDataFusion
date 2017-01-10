package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.Collection;
import java.util.HashSet;

/**
 * Feature concept implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractFeatureConcept extends Subject implements IFeatureConcept {

	private Collection<IFeatureEntity> entities;
	private Collection<IFeatureType> types;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param concept feature concept
	 * @param description feature concept description
	 */
	public AbstractFeatureConcept(IIdentifier identifier, Object concept, IMetadataForData description){
		super(identifier, concept, description);
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
	public void addRelatedType(IFeatureType type){
		if(types == null)
			types = new HashSet<>();
		types.add(type);
	}
	
	/**
	 * adds an associated feature entity
	 * @param entity associated entity
	 */
	public void addRelatedEntity(IFeatureEntity entity){
		if(entities == null)
			entities = new HashSet<>();
		entities.add(entity);
	}

}
