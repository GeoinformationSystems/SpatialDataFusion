package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IMeasurementData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * observation object
 */
public interface IObservation extends IFeature {

    /**
     * get observed feature of interest
     * @return feature of interest
     */
	IFeatureEntity getFeatureOfInterest();

	/**
	 * get observed property
	 * @return observed property
	 */
    IResource getObservedProperty();
	
	/**
	 * get time when the observation's measurement applies
	 * @return phenomenon time
	 */
    IMeasurementData getPhenomenonTime();

    /**
     * get measurement for this observation
     * @return observation measurement
     */
    IMeasurementData getMeasurement();
	
}
