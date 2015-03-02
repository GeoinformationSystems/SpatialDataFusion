package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;

/**
 * relation confidence measurement
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IConfidenceMeasurement extends IRelationMeasurement {

	@Override
	public IConfidenceMeasurementDescription getDescription();
	
}
