package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

public interface IFeatureInstance extends IFeatureView {

	/**
	 * get feature representations for this instance
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentation> representations();
	
	/**
	 * get feature concept for this instance
	 * @return related feature concept
	 */
	public IFeatureConcept concept();
	
}
