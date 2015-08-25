package de.tudresden.gis.fusion.data;

public interface IRange<T extends Comparable<T>> {

	/**
	 * get value range
	 * @return value range
	 */
	public T[] valueRange();
	
	/**
	 * get continuous flag
	 * @return true, if measurement range has continuous values
	 */
	public boolean continuous();
	
	/**
	 * get min of range (based on comparison of range values)
	 * @return range min
	 */
	public T min();

	/**
	 * get max of range (based on comparison of range values)
	 * @return range max
	 */
	public T max();

	/**
	 * check whether the range contains a given value
	 * @param target target value to check
	 * @return true, if the target value is within the range
	 */
	public boolean contains(T target);
	
}
