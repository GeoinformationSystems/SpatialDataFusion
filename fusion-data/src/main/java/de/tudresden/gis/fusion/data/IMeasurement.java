package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;

/**
 * basic measurement object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurement extends ILiteralData,Comparable<IMeasurement> {
	
	@Override
	public IMeasurementDescription getDescription();
	
}
