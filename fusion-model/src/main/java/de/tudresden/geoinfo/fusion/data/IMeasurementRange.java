package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

/**
 * Basic range of measurements
 */
public interface IMeasurementRange<T extends Comparable<T>> {

    /**
     * get value range
     *
     * @return value range
     */
    @NotNull
    SortedSet<T> getRangeMembers();

    /**
     * get continuous flag
     *
     * @return true, if measurement range has continuous values
     */
    boolean isContinuous();

    /**
     * get min of range (based on comparison of range values)
     *
     * @return range min
     */
    @NotNull
    T getMin();

    /**
     * get max of range (based on comparison of range values)
     *
     * @return range max
     */
    @NotNull
    T getMax();

    /**
     * check whether the range contains a given value
     *
     * @param target target value to check
     * @return true, if the target value is within the range
     */
    boolean contains(T target);

}
