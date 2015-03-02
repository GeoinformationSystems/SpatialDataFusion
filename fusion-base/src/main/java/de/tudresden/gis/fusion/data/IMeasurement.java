package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IMeasurementDescription;

public interface IMeasurement extends IComplexData {
	
	/**
	 * get process resource that created the measurement
	 * @return process resource
	 */
	public IIdentifiableResource getProcess();
	
	/**
	 * get measurement value
	 * @return measurement value
	 */
	public IMeasurementValue<?> getMeasurementValue();
	
	@Override
	public IMeasurementDescription getDescription();
	
}
