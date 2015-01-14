package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;

/**
 * relation measurement
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurement extends IComplexData {
	
	/**
	 * get relation type for this measurement
	 * @return relation type
	 */
	public IRelationType getRelationType();
	
	public IMeasurementValue<?> getMeasurementValue();
	
	public IMeasurementDescription getDescription();
	
}
