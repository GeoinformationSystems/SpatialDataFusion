package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.metadata.operation.ISimilarityMeasurementOperationProfile;

public interface ISimilarityMeasurementOperation extends IRelationMeasurementOperation {

	@Override
	public ISimilarityMeasurementOperationProfile getProfile();
	
}
