package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

/**
 * feature representation view of a feature
 */
public interface IFeatureRepresentation extends IFeatureView {
	
	/**
	 * get feature type for this representation
	 * @return related feature type
	 */
    IFeatureType getRelatedType();
	
	/**
	 * get instance represented by this representation
	 * @return related feature instance
	 */
    IFeatureEntity getRelatedEntity();

    /**
     * get feature property by identifier
     * @param identifier feature property identifier
     * @return property or null, if identifier is not linked to a property
     */
    Object getProperty(IIdentifier identifier);

    /**
     * get default feature geometry
     * @return geometry or null, if feature has no geometry
     */
    Object getDefaultGeometry();

    /**
     * get bounding box for the feature
     * @return bounding box
     */
    Object getBounds();

    /**
     * get reference system for the feature
     * @return reference system
     */
    Object getReferenceSystem();
	
}
