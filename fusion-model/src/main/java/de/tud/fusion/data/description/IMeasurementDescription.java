package de.tud.fusion.data.description;

import de.tud.fusion.data.rdf.IResource;

/**
 * description of a measurement data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementDescription extends IDataDescription {
	
	/**
	 * get measurement type
	 * @return measurement type
	 */
	public IResource getMeasurementType();

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
