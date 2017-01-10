package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * Description of a data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMetadataForData extends IMetadata {
	
	/**
	 * get data type resource
	 * @return data type resource
	 */
    IResource getDataType();
	
}
