package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

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
	public IIdentifiableResource getUnitOfMeasurement();
	
}
