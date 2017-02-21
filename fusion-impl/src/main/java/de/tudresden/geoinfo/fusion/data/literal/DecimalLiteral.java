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
 * Decimal literal implementation
 */
public class DecimalLiteral extends Measurement<Double> {

    private static IResource TYPE = Objects.DECIMAL.getResource();

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                decimal value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public DecimalLiteral(@Nullable IIdentifier identifier, double value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Double> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                double value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public DecimalLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, double value, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Double> measurementRange, @NotNull IResource uom) {
        super(identifier, value, new Metadata(title, description), TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param value      decimal value
     */
    public DecimalLiteral(@Nullable IIdentifier identifier, double value) {
        this(identifier, value, null, null, DecimalLiteral.getMaxRange(), Units.UNKNOWN.getResource());
    }

    /**
     * constructor, creates random identifier
     *
     * @param value decimal value
     */
    public DecimalLiteral(double value) {
        this(null, value);
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
