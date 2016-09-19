package de.tud.fusion.data.feature;

import java.util.Collection;
import java.util.Set;

/**
 * feature type view of a feature
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureTypeView extends IFeatureView {

	/**
	 * get property identifier for this feature type
	 * @return set of property identifiers
	 */
	public Set<String> getPropertyIdentifier();
	
	/**
	 * get feature concept implemented by this type
	 * @return related feature concept
	 */
	public IFeatureConceptView getRelatedConcept();
	
	/**
	 * get representations implementing this type
	 * @return related feature representations
	 */
	public Collection<IFeatureRepresentationView> getRelatedRepresentations();
	
}
