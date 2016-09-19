package de.tud.fusion.data.feature;

/**
 * feature representation view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureRepresentationView extends IFeatureView {
	
	/**
	 * get feature property by identifier
	 * @param identifier property identifier
	 * @return feature property object
	 */
	public Object getProperty(String identifier);
	
	/**
	 * get default geometry of the feature representation
	 * @return feature geometry object
	 */
	public Object getDefaultGeometry();
	
	/**
	 * get feature type for this representation
	 * @return related feature type
	 */
	public IFeatureTypeView getRelatedType();
	
	/**
	 * get instance represented by this representation
	 * @return related feature instance
	 */
	public IFeatureEntityView getRelatedEntity();
	
}
