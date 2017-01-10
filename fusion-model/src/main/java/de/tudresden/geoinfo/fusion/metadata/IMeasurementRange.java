package de.tudresden.geoinfo.fusion.metadata;

import java.util.SortedSet;

/**
 * Basic range of measurements
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementRange<T extends Comparable<T>> {

	/**
	 * get value range
	 * @return value range
	 */
	SortedSet<T> getRangeMembers();
	
	/**
	 * get continuous flag
	 * @return true, if measurement range has continuous values
	 */
    boolean isContinuous();
	
	/**
	 * get min of range (based on comparison of range values)
	 * @return range min
	 */
    T getMin();

	/**
	 * get max of range (based on comparison of range values)
	 * @return range max
	 */
    T getMax();

	/**
	 * check whether the range contains a given value
	 * @param target target value to check
	 * @return true, if the target value is within the range
	 */
    boolean contains(T target);
	
}
