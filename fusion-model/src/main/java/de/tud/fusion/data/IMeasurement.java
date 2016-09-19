package de.tud.fusion.data;

import de.tud.fusion.data.description.IMeasurementDescription;

/**
 * Basic measurement object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurement extends ILiteralData,Comparable<IMeasurement> {
	
	@Override
	public IMeasurementDescription getDescription();
	
}
