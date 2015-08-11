package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

public interface IFeatureInstance extends IFeatureView {

	/**
	 * get feature representations for this instance
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentation> getRepresentations();
	
	/**
	 * get feature type for this instance
	 * @return related feature type
	 */
	public IFeatureType getType();
	
}
