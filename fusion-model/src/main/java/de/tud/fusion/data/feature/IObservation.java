package de.tud.fusion.data.feature;

import de.tud.fusion.data.IMeasurement;
import de.tud.fusion.data.rdf.IResource;

/**
 * observation object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IObservation extends IFeature {

	/**
	 * get measurement of this observation
	 * @return observation measurement
	 */
	public IMeasurement getMeasurement();
	
	/**
	 * get feature of interest for this observation
	 * @return feature of interest
	 */
	public IFeatureEntityView getFeatureOfInterest();
	
	/**
	 * get observed property
	 * @return observed property
	 */
	public IResource getObservedProperty();
	
	/**
	 * get time when the observation's measurement applies
	 * @return phenomenon time
	 */
	public IMeasurement getPhenomenonTime();
	
}
