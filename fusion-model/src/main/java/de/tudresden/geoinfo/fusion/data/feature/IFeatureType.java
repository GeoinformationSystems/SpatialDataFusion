package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.Collection;
import java.util.Set;

/**
 * feature type view of a feature
 */
public interface IFeatureType extends IFeatureView {
	
	/**
	 * get feature concept implemented by this type
	 * @return related feature concept
	 */
    IFeatureConcept getRelatedConcept();
	
	/**
	 * get representations implementing this type
	 * @return related feature representations
	 */
    Collection<IFeatureRepresentation> getRelatedRepresentations();

    /**
     * get feature property identifier
     * @return property identifier
     */
    Set<IIdentifier> getProperties();
	
}
