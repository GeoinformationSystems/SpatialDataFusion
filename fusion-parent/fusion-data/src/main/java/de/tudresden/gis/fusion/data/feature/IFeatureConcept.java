package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

public interface IFeatureConcept extends IFeatureView {

	/**
	 * get feature types based on this concept
	 * @return related feature types
	 */
	public Collection<IFeatureType> getTypes();
	
	/**
	 * get Instances defined by this concept
	 * @return related feature instances
	 */
	public Collection<IFeatureInstance> getInstances();
	
}
