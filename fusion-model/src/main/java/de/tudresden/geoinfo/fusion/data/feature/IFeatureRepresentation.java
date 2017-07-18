package de.tudresden.geoinfo.fusion.data.feature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * feature representation view of a feature
 */
public interface IFeatureRepresentation extends IFeatureView {

    /**
     * get feature type for this representation
     *
     * @return related feature type
     */
    @NotNull
    IFeatureType getRelatedType();

    /**
     * get instance represented by this representation
     *
     * @return related feature instance
     */
    @NotNull
    IFeatureEntity getRelatedEntity();

    /**
     * get feature property by identifier
     *
     * @param identifier feature property identifier
     * @return property or null, if identifier is not linked to a property
     */
    @Nullable
    Object getProperty(@NotNull String identifier);

    /**
     * get default feature geometry object
     *
     * @return geometry or null, if feature has no geometry
     */
    @Nullable
    Object getDefaultGeometry();

    /**
     * get bounding box for the feature
     *
     * @return bounding box or null, if getDefaultGeometry() returns null
     */
    @Nullable
    Envelope getBounds();

    /**
     * get reference system for the feature
     *
     * @return reference system
     */
    @Nullable
    CoordinateReferenceSystem getReferenceSystem();

}
