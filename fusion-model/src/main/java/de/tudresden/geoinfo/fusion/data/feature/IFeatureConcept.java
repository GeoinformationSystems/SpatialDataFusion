package de.tudresden.geoinfo.fusion.data.feature;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * feature concept view of a feature
 */
public interface IFeatureConcept extends IFeatureView {

    /**
     * get feature types based on this concept
     *
     * @return related feature types
     */
    @NotNull
    Collection<IFeatureType> getRelatedTypes();

    /**
     * get Instances defined by this concept
     *
     * @return related feature instances
     */
    @NotNull
    Collection<IFeatureEntity> getRelatedEntities();

}
