package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * description of a measurement data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMetadataForMeasurement extends IMetadataForData {

	/**
	 * get measurement operation
	 * @return measurement operation
	 */
    IResource getMeasurementOperation();
	
	/**
	 * get unit of measurement
	 * @return measurement unit identifier
	 */
    IResource getUnitOfMeasurement();
	
}
