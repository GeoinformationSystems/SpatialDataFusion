package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

public interface IFeatureType extends IFeatureView {

	/**
	 * get feature concept implemented by this type
	 * @return related feature concept
	 */
	public IFeatureConcept concept();
	
	/**
	 * get representations implementing this type
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentation> representations();
	
}
