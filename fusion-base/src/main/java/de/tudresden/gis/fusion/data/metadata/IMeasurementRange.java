package de.tudresden.gis.fusion.data.metadata;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

/**
 * measurement range definition interface
 * @author Stefan
 * 
 */
public interface IMeasurementRange<T> extends IRDFTripleSet {
	
	/**
	 * get measurement range (not necessarily sorted)
	 * @return measurement range
	 */
	public IMeasurementValue<T>[] getRange();
	
	/**
	 * get continuous flag
	 * @return true, if measurement range has continuous values
	 */
	public boolean isContinuous();
	
	/**
	 * get min of range (based on comparison of range values)
	 * @return range min
	 */
	public IMeasurementValue<T> getMin();

	/**
	 * get max of range (based on comparison of range values)
	 * @return range max
	 */
	public IMeasurementValue<T> getMax();

	/**
	 * check whether a range contains a given value
	 * @param target target value to check for
	 * @return true, if the range contains the target value
	 */
	public boolean contains(IMeasurementValue<T> target);
	
}
