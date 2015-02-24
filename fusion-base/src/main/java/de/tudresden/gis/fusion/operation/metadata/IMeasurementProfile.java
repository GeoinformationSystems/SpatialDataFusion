package de.tudresden.gis.fusion.operation.metadata;

import java.util.Collection;

import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;

public interface IMeasurementProfile extends IOperationProfile {

	public Collection<IMeasurementDescription> getSupportedMeasurements();
	
}
