package de.tudresden.geoinfo.fusion.data.feature;

import de.tudresden.geoinfo.fusion.data.IMeasurement;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * observation object
 */
public interface IObservation extends IFeature {

    /**
     * get observed feature of interest
     *
     * @return feature of interest
     */
    @NotNull
    IFeatureEntity getFeatureOfInterest();

    /**
     * get observed property
     *
     * @return observed property
     */
    @NotNull
    IResource getObservedProperty();

    /**
     * get time when the observation's measurement applies
     *
     * @return phenomenon time
     */
    @Nullable
    IMeasurement getPhenomenonTime();

    /**
     * get measurement for this observation
     *
     * @return observation measurement
     */
    @NotNull
    IMeasurement getMeasurement();

}
