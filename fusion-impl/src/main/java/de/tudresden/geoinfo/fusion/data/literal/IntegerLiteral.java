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
 * Integer literal implementation
 */
public class IntegerLiteral extends LiteralData<Integer> {

    public final static IRDFProperty TYPE = Objects.INTEGER.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public IntegerLiteral(@NotNull IIdentifier identifier, int value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value                literal value
     */
    public IntegerLiteral(int value) {
        this(new ResourceIdentifier(), value, null);
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

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
