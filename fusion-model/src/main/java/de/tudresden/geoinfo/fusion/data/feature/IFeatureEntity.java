package de.tudresden.geoinfo.fusion.data.feature;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * feature entity view of a feature
 */
public interface IFeatureEntity extends IFeatureView {

    /**
     * get feature representations for this instance
     *
     * @return related feature representations
     */
    @NotNull
    Collection<IFeatureRepresentation> getRelatedRepresentations();

    /**
     * get feature concept for this instance
     *
     * @return related feature concept
     */
    @NotNull
    IFeatureConcept getRelatedConcept();

}
