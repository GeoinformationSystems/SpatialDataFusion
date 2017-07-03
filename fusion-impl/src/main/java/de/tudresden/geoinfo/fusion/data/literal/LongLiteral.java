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
 * Boolean literal implementation
 */
public class LongLiteral extends Measurement<Long> {

    private static IResource TYPE = Objects.LONG.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public LongLiteral(@Nullable IIdentifier identifier, long value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value                literal value
     * @param measurementOperation associated measurement operation
     */
    public LongLiteral(long value, @Nullable IResource measurementOperation) {
        this(null, value, null, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public LongLiteral(long value) {
        this(value, null);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Long> getMaxRange() {
        return getRange(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * get positive range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Long> getPositiveRange() {
        return getRange(0, Long.MAX_VALUE);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Long> getRange(long min, long max) {
        SortedSet<Long> range = new TreeSet<>();
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
