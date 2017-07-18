package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.operation.IOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic measurement object
 */
public interface IMeasurement<T extends Comparable<T>> extends IData, Comparable<IMeasurement<T>> {

    @NotNull
    T resolve();

    /**
     * get measurement operation
     *
     * @return measurement operation
     */
    @Nullable
    IOperation getMeasurementOperation();

    /**
     * get associated measurement range
     *
     * @return measurement range
     */
    @Nullable
    IMeasurementRange<T> getMeasurementRange();

    /**
     * get unit of measurement
     *
     * @return measurement unit identifier
     */
    @Nullable
    IIdentifier getUnitOfMeasurement();

}
