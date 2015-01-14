package de.tudresden.gis.fusion.data;

public interface IMeasurementValue<T> extends ISimpleData,Comparable<IMeasurementValue<T>> {

	public T getValue();
	
}
