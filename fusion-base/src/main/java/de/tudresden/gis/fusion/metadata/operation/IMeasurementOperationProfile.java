package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;

import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;

/**
 * measurement operation profile
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IMeasurementOperationProfile extends IOperationProfile {

	/**
	 * get supported measurements
	 * @return supported measurements
	 */
	public Collection<? extends IMeasurementDescription> getSupportedMeasurements();
	
}
