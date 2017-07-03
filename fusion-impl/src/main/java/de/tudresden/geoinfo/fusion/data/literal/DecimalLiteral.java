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
 * Decimal literal implementation
 */
public class DecimalLiteral extends Measurement<Double> {

    private static IResource TYPE = Objects.DECIMAL.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public DecimalLiteral(@Nullable IIdentifier identifier, double value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value                literal value
     * @param measurementOperation associated measurement operation
     */
    public DecimalLiteral(double value, @Nullable IResource measurementOperation) {
        this(null, value, null, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public DecimalLiteral(double value) {
        this(value, null);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Double> getMaxRange() {
        return getRange(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * get positive range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Double> getPositiveRange() {
        return getRange(0d, Double.MAX_VALUE);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Double> getRange(double min, double max) {
        SortedSet<Double> range = new TreeSet<>();
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
