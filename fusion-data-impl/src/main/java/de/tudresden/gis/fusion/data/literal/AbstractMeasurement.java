package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;

public abstract class AbstractMeasurement<T> implements IMeasurement {
	
	T object;
	IMeasurementDescription description;
	
	public AbstractMeasurement(T object, IMeasurementDescription description){
		this.object = object;
		this.description = description;
	}

	@Override
	public T resolve() {
		return object;
	}

	@Override
	public IMeasurementDescription getDescription() {
		return description;
	}
	
	@Override
	public String toString(){
		return resolve().toString();
	}

}
