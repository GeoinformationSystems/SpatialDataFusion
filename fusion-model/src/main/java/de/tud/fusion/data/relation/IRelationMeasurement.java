package de.tud.fusion.data.relation;

import de.tud.fusion.data.IMeasurement;

/**
 * Relation measurement object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurement extends IMeasurement {

	/**
	 * get reference object for this relation measurement
	 * @return measurement reference
	 */
	public Object getReference();
	
	/**
	 * get target object for this relation measurement
	 * @return measurement target
	 */
	public Object getTarget();
	
}
