package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.metadata.operation.IMeasurementOperationProfile;

/**
 * relation measurement operation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementOperation extends IOperation {

	@Override
	public IMeasurementOperationProfile getProfile();
	
}
