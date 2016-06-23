package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

/**
 * feature type view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureType extends IFeatureView {

	/**
	 * get feature concept implemented by this type
	 * @return related feature concept
	 */
	public IFeatureConcept getRelatedConcept();
	
	/**
	 * get representations implementing this type
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentation> getRelatedRepresentations();
	
}
