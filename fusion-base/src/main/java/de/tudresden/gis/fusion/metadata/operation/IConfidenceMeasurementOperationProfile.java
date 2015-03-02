package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;

import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;

/**
 * relation measurement operation profile
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IConfidenceMeasurementOperationProfile extends IRelationMeasurementOperationProfile {

	@Override
	public Collection<? extends IConfidenceMeasurementDescription> getSupportedMeasurements();
	
}
