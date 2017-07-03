package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.Measurement;
import de.tudresden.geoinfo.fusion.data.MeasurementRange;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Integer literal implementation
 */
public class IntegerLiteral extends Measurement<Integer> {

    private static IResource TYPE = Objects.INTEGER.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public IntegerLiteral(@Nullable IIdentifier identifier, int value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value                literal value
     * @param measurementOperation associated measurement operation
     */
    public IntegerLiteral(int value, @Nullable IResource measurementOperation) {
        this(null, value, null, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public IntegerLiteral(int value) {
        this(value, null);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Integer> getMaxRange() {
        return getRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * get positive range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Integer> getPositiveRange() {
        return getRange(0, Integer.MAX_VALUE);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Integer> getRange(int min, int max) {
        SortedSet<Integer> range = new TreeSet<>();
        range.add(min);
        range.add(max);
        return new MeasurementRange<>(range, true);
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
