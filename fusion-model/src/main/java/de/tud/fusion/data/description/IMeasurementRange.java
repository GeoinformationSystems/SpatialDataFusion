package de.tud.fusion.data.description;

import de.tud.fusion.data.IMeasurement;

/**
 * Basic range of measurements
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementRange {

	/**
	 * get value range
	 * @return value range
	 */
	public IMeasurement[] getMembers();
	
	/**
	 * get continuous flag
	 * @return true, if measurement range has continuous values
	 */
	public boolean isContinuous();
	
	/**
	 * get min of range (based on comparison of range values)
	 * @return range min
	 */
	public IMeasurement getMin();

	/**
	 * get max of range (based on comparison of range values)
	 * @return range max
	 */
	public IMeasurement getMax();

	/**
	 * check whether the range contains a given value
	 * @param target target value to check
	 * @return true, if the target value is within the range
	 */
	public boolean contains(IMeasurement target);
	
}
