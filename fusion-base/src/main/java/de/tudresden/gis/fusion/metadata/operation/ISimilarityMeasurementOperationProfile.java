package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;

import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;

/**
 * relation measurement operation profile
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ISimilarityMeasurementOperationProfile extends IRelationMeasurementOperationProfile {

	@Override
	public Collection<? extends ISimilarityMeasurementDescription> getSupportedMeasurements();
	
}
