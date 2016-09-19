package de.tud.fusion.data.feature;

import java.util.Collection;

/**
 * feature concept view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureConceptView extends IFeatureView {

	/**
	 * get feature types based on this concept
	 * @return related feature types
	 */
	public Collection<IFeatureTypeView> getRelatedTypes();
	
	/**
	 * get Instances defined by this concept
	 * @return related feature instances
	 */
	public Collection<IFeatureEntityView> getRelatedEntities();
	
}
