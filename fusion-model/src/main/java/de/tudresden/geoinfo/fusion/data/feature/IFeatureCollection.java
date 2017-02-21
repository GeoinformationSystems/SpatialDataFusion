package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IDataCollection;
import org.jetbrains.annotations.Nullable;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * feature collection
 */
public interface IFeatureCollection<T extends IFeature> extends IDataCollection<T> {

    /**
     * get bounding box for the feature collection
     *
     * @return bounding box
     */
    @Nullable
    Envelope getBounds();

    /**
     * get reference system for the feature collection
     *
     * @return reference system
     */
    @Nullable
    CoordinateReferenceSystem getReferenceSystem();

}
