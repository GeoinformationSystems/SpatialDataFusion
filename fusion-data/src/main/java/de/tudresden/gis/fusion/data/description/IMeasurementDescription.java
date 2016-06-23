package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * description of a measurement data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementDescription extends IDataDescription {

	/**
	 * get range for the measurement
	 * @return measurement range
	 */
	public IMeasurementRange getRange();
	
	/**
	 * get unit of measurement
	 * @return measurement unit identifier
	 */
	public IResource getUnitOfMeasurement();
	
}
