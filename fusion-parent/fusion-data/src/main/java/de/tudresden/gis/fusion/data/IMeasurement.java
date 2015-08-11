package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IMeasurement<T extends Comparable<T>> extends Comparable<T> {

	/**
	 * get measurement value
	 * @return measurement value
	 */
	public T getValue();
	
	/**
	 * get range for this measurement type
	 * @return measurement range
	 */
	public IRange<T> getRange();
	
	/**
	 * get unit of measurement
	 * @return measurement unit
	 */
	public IRDFIdentifiableResource getUnitOfMeasurement();
	
}
