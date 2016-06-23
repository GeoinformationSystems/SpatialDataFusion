package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

/**
 * feature entity view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
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
