package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.metadata.operation.IConfidenceMeasurementOperationProfile;

public interface IConfidenceMeasurementOperation extends IRelationMeasurementOperation {

	@Override
	public IConfidenceMeasurementOperationProfile getProfile();
	
}
