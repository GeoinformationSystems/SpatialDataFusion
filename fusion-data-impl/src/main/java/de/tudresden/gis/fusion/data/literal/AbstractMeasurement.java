package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;

/**
 * abstract measurement implementation
 * @author Stefan Wiemann, TU Dresden
 *
 * @param <T> measurement value type
 */
public abstract class AbstractMeasurement<T> implements IMeasurement {
	
	/**
	 * measurement value
	 */
	T object;
	
	/**
	 * measurement description
	 */
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
