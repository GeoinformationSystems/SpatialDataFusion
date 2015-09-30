package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IMeasurementDescription;

public interface IMeasurement extends ILiteralData,Comparable<IMeasurement> {
	
	@Override
	public IMeasurementDescription getDescription();
	
}
