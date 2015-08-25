package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.IRange;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IMeasurementDescription extends IDataDescription {

	/**
	 * get range for the measurement
	 * @return measurement range
	 */
	public IRange<?> range();
	
	/**
	 * get unit of measurement
	 * @return measurement unit
	 */
	public IRDFIdentifiableResource unitOfMeasurement();
	
}
