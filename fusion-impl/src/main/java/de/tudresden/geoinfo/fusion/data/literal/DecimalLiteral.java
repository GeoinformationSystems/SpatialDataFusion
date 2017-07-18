package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.*;
import de.tudresden.geoinfo.fusion.data.metadata.MeasurementRange;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Decimal literal implementation
 */
public class DecimalLiteral extends LiteralData<Double> {

    public final static IRDFProperty TYPE = Objects.DECIMAL.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public DecimalLiteral(@NotNull IIdentifier identifier, double value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public DecimalLiteral(double value) {
        this(new ResourceIdentifier(), value, null);
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

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
