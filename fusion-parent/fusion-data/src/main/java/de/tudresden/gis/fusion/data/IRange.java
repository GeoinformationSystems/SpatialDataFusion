package de.tudresden.gis.fusion.data;

public interface IRange<T extends Comparable<T>> {

	/**
	 * get measurement range
	 * @return measurement range
	 */
	public T[] getRange();
	
	/**
	 * get continuous flag
	 * @return true, if measurement range has continuous values
	 */
	public boolean isContinuous();
	
	/**
	 * get min of range (based on comparison of range values)
	 * @return range min
	 */
	public T getMin();

	/**
	 * get max of range (based on comparison of range values)
	 * @return range max
	 */
	public T getMax();

	/**
	 * check whether the range contains a given value
	 * @param target target value to check
	 * @return true, if the target value is within the range
	 */
	public boolean contains(T target);
	
}
