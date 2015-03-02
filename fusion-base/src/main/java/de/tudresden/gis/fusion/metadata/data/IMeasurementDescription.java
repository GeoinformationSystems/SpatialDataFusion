package de.tudresden.gis.fusion.metadata.data;

/**
 * measurement description
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementDescription extends IDescription {
	
	/**
	 * get possible range of measurements
	 * @return measurement range
	 */
	public IMeasurementRange<?> getMeasurementRange();
	
}
