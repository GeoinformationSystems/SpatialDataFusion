package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.feature.IFeatureInstance;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IObservation extends IRDFIdentifiableResource {

	/**
	 * get measurement of this observation
	 * @return observation measurement
	 */
	public IMeasurementValue<?> measurement();
	
	/**
	 * get feature of interest for this observation
	 * @return feature of interest
	 */
	public IFeatureInstance featureOfInterest();
	
	/**
	 * get observed property
	 * @return observed property
	 */
	public IRDFIdentifiableResource observedProperty();
	
	/**
	 * get time when the observation's measurement applies (UNIX time)
	 * @return phenomenon time
	 */
	public IMeasurementValue<Long> phenomenonTime();
	
}
