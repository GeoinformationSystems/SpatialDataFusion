package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

public interface IFeatureEntity extends IFeatureView {

	/**
	 * get feature representations for this instance
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentation> getRelatedRepresentations();
	
	/**
	 * get feature concept for this instance
	 * @return related feature concept
	 */
	public IFeatureConcept getRelatedConcept();
	
}
