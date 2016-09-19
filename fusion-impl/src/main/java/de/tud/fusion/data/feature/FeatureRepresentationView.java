package de.tud.fusion.data.feature;

import org.opengis.coverage.Coverage;
import org.opengis.feature.Feature;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * feature representation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class FeatureRepresentationView extends ResourceData implements IFeatureRepresentationView {
	
	/**
	 * feature type associated with this representation
	 */
	private IFeatureTypeView type;
	
	/**
	 * entity represented
	 */
	private IFeatureEntityView entity;
	
	/**
	 * constructor
	 * @param identifier feature identifier
	 * @param feature feature representation
	 * @param description feature description
	 */
	public FeatureRepresentationView(String identifier, Feature feature, IDataDescription description){
		super(identifier, feature, description);
	}
	
	/**
	 * constructor
	 * @param identifier coverage identifier
	 * @param feature coverage representation
	 * @param description coverage description
	 */
	public FeatureRepresentationView(String identifier, Coverage coverage, IDataDescription description){
		super(identifier, coverage, description);
	}
	
	@Override
	public Object getProperty(String identifier) {
		if(resolve() instanceof Feature)
			return ((Feature) resolve()).getProperty(identifier);
		return null;
	}

	@Override
	public Object getDefaultGeometry() {
		if(resolve() instanceof Feature)
			return ((Feature) resolve()).getDefaultGeometryProperty().getValue();
		return null;
	}

	@Override
	public IFeatureTypeView getRelatedType() {
		return type;
	}

	@Override
	public IFeatureEntityView getRelatedEntity() {
		return entity;
	}
	
	/**
	 * set feature type
	 * @param type associated type
	 */
	public void setType(IFeatureTypeView type){
		this.type = type;
	}
	
	/**
	 * set feature entity
	 * @param entity associated entity
	 */
	public void setEntity(IFeatureEntityView entity){
		this.entity = entity;
	}

}
