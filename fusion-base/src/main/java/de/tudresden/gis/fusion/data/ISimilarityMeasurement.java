package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;

/**
 * relation similarity measurement
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ISimilarityMeasurement extends IRelationMeasurement {

	@Override
	public ISimilarityMeasurementDescription getDescription();
	
}
