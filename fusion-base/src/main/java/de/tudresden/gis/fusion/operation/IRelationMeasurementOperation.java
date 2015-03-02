package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.metadata.operation.IRelationMeasurementOperationProfile;

public interface IRelationMeasurementOperation extends IMeasurementOperation {

	@Override
	public IRelationMeasurementOperationProfile getProfile();
	
}
