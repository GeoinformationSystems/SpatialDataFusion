package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IDataCollection;

/**
 * feature collection
 */
public interface IFeatureCollection<T extends IFeature> extends IDataCollection<T> {

    /**
     * get bounding box for the feature collection
     * @return bounding box
     */
    Object getBounds();

    /**
     * get reference system for the feature collection
     * @return reference system
     */
    Object getReferenceSystem();

}
