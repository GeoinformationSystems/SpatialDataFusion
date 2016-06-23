package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

/**
 * feature concept view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureConcept extends IFeatureView {

	/**
	 * get feature types based on this concept
	 * @return related feature types
	 */
	public Collection<IFeatureType> getRelatedTypes();
	
	/**
	 * get Instances defined by this concept
	 * @return related feature instances
	 */
	public Collection<IFeatureEntity> getRelatedEntities();
	
}
