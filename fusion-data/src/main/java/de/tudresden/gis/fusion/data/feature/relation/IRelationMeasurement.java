package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.IMeasurement;

/**
 * relation measurement object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurement extends IMeasurement {

	/**
	 * get reference for this relation measurement
	 * @return measurement reference
	 */
	public Object getReference();
	
	/**
	 * get target for this relation measurement
	 * @return measurement target
	 */
	public Object getTarget();
	
}
