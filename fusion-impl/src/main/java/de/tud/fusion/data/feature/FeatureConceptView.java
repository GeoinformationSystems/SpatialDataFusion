package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.HashSet;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * Feature concept implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureConceptView extends ResourceData implements IFeatureConceptView {
	
	/**
	 * entities defined by this concept
	 */
	private Collection<IFeatureEntityView> entities;
	
	/**
	 * types associated with this concept
	 */
	private Collection<IFeatureTypeView> types;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param concept feature concept
	 * @param description feature concept description
	 */
	public FeatureConceptView(String identifier, Object concept, IDataDescription description){
		super(identifier, concept, description);
	}
	
	@Override
	public Collection<IFeatureEntityView> getRelatedEntities() {
		return entities;
	}
	
	@Override
	public Collection<IFeatureTypeView> getRelatedTypes() {
		return types;
	}
	
	/**
	 * adds an associated feature type
	 * @param type associated type
	 */
	public void addType(IFeatureTypeView type){
		if(types == null)
			types = new HashSet<IFeatureTypeView>();
		types.add(type);
	}
	
	/**
	 * adds an associated feature entity
	 * @param entity associated entity
	 */
	public void addEntity(IFeatureEntityView entity){
		if(entities == null)
			entities = new HashSet<IFeatureEntityView>();
		entities.add(entity);
	}

}
