package de.tudresden.gis.fusion.data;

public interface IMeasurement<T extends Comparable<T>> extends IData,Comparable<T> {

	/**
	 * get range for this measurement type
	 * @return measurement range
	 */
	public IRange<T> getRange();
	
}
