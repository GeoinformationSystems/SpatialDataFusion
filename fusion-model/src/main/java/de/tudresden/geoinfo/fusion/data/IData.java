package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

/**
 * Basic data object
 */
public interface IData {

	/**
	 * resolve data object or value
	 * @return Java object represented by this resource
	 */
    Object resolve();
	
	/**
	 * get description of this object or value
	 * @return resource description
	 */
    IMetadataForData getMetadata();
	
}
