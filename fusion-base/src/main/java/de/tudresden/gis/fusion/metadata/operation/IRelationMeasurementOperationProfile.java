package de.tudresden.gis.fusion.metadata.operation;

import java.util.Collection;

import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;

/**
 * relation measurement operation profile
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurementOperationProfile extends IMeasurementOperationProfile {

	@Override
	public Collection<? extends IRelationMeasurementDescription> getSupportedMeasurements();
	
}
