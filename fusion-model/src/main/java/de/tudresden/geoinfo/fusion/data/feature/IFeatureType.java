package de.tudresden.geoinfo.fusion.data.feature;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/**
 * feature type view of a feature
 */
public interface IFeatureType extends IFeatureView {

    /**
     * get feature concept implemented by this type
     *
     * @return related feature concept
     */
    @NotNull
    IFeatureConcept getRelatedConcept();

    /**
     * get representations implementing this type
     *
     * @return related feature representations
     */
    @NotNull
    Collection<IFeatureRepresentation> getRelatedRepresentations();

    /**
     * get feature property identifier
     *
     * @return property identifier
     */
    @NotNull
    Set<String> getProperties();

}
