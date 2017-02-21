package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Measurement range implementation
 */
public class MeasurementRange<T extends Comparable<T>> implements IMeasurementRange<T> {

    private SortedSet<T> members;
    private boolean continuous;

    /**
     * constructor
     *
     * @param members    range member
     * @param continuous flag: continuous range
     */
    public MeasurementRange(@NotNull SortedSet<T> members, boolean continuous) {
        this.members = members;
        this.continuous = continuous;
    }

    /**
     * constructor
     *
     * @param members    range member
     * @param continuous flag: continuous range
     */
    public MeasurementRange(@NotNull T[] members, boolean continuous) {
        this(new TreeSet<>(Arrays.asList(members)), continuous);
    }

    @NotNull
    @Override
    public SortedSet<T> getRangeMembers() {
        return members;
    }

    @Override
    public boolean isContinuous() {
        return continuous;
    }

    @NotNull
    @Override
    public T getMin() {
        return members.first();
    }

    @NotNull
    @Override
    public T getMax() {
        return members.last();
    }

    @Override
    public boolean contains(@NotNull T target) {
        if (!isContinuous())
            return partOfRange(target);
        else
            return inBetweenRange(target);
    }

    /**
     * check if value is part of element range
     *
     * @param target target value
     * @return true, if target value is member of range
     */
    private boolean partOfRange(@NotNull T target) {
        for (T value : getRangeMembers()) {
            if (value.compareTo(target) == 0)
                return true;
        }
        return false;
    }

    /**
     * check if value is between min and max
     *
     * @param target target value
     * @return true, if min < value < max
     */
    private boolean inBetweenRange(@NotNull T target) {
        return getMin().compareTo(target) >= 0 && getMax().compareTo(target) <= 0;
    }

}
