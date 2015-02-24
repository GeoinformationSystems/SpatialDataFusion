package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.operation.metadata.IMeasurementProfile;

/**
 * relation measurement operation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurement extends IOperation {

	@Override
	public IMeasurementProfile getProfile();
	
}
