package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;

/**
 * relation measurement
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurement extends IMeasurement {
	
	@Override
	public IRelationMeasurementDescription getDescription();
	
}
