package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import org.jetbrains.annotations.NotNull;

/**
 * Basic operation object
 */
public interface IMeasurementOperation<T extends Comparable<T>> extends IOperation {

    /**
     * get range for the measurement operation
     *
     * @return measurement range
     */
    @NotNull
    IMeasurementRange<T> getMeasurementRange();

}
