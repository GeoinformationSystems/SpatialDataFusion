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
 * Integer literal implementation
 */
public class IntegerLiteral extends Measurement<Integer> {

    private static IResource TYPE = Objects.INTEGER.getResource();

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                integer value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public IntegerLiteral(@Nullable IIdentifier identifier, int value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Integer> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                int value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public IntegerLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, int value, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Integer> measurementRange, @NotNull IResource uom) {
        super(identifier, value, new Metadata(title, description), TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param value      integer value
     */
    public IntegerLiteral(@Nullable IIdentifier identifier, int value) {
        this(identifier, value, null, null, IntegerLiteral.getMaxRange(), Units.UNKNOWN.getResource());
    }

    /**
     * constructor, creates random identifier
     *
     * @param value integer value
     */
    public IntegerLiteral(int value) {
        this(null, value);
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
