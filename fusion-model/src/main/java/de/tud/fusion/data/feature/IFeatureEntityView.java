package de.tud.fusion.data.feature;

import java.util.Collection;

/**
 * feature entity view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureEntityView extends IFeatureView {

	/**
	 * get feature representations for this instance
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentationView> getRelatedRepresentations();
	
	/**
	 * get feature concept for this instance
	 * @return related feature concept
	 */
	public IFeatureConceptView getRelatedConcept();
	
}
