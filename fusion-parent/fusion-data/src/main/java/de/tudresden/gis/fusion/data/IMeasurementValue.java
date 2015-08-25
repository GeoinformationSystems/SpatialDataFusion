package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;

public interface IMeasurementValue<T extends Comparable<T>> extends IData,Comparable<T> {

	@Override
	public T value();
	
	@Override
	public IMeasurementDescription description();
	
}
