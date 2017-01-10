package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurementOperation;

/**
 * Basic measurement operation object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementOperation extends IResource {

	/**
	 * returns operation metadata
	 * @return operation metadata
	 */
	IMetadataForMeasurementOperation getMetadata();
	
}
