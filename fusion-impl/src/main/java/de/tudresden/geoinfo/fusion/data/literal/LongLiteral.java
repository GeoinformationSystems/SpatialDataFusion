package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.*;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
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
     * constructor
     *
     * @param identifier           data identifier
     * @param value                long value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public LongLiteral(@Nullable IIdentifier identifier, long value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Long> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                long value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public LongLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, long value, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Long> measurementRange, @NotNull IResource uom) {
        super(identifier, value, new Metadata(title, description), TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param value      long value
     */
    public LongLiteral(@Nullable IIdentifier identifier, long value) {
        this(identifier, value, null, null, LongLiteral.getMaxRange(), Units.UNKNOWN.getResource());
    }

    /**
     * constructor, creates random identifier
     *
     * @param value long value
     */
    public LongLiteral(long value) {
        this(null, value);
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
