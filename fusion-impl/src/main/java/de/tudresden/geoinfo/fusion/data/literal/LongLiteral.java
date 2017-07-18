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
 * Boolean literal implementation
 */
public class LongLiteral extends LiteralData<Long> {

    public final static IRDFProperty TYPE = Objects.LONG.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public LongLiteral(@NotNull IIdentifier identifier, long value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value                literal value
     */
    public LongLiteral(long value) {
        this(new ResourceIdentifier(), value, null);
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

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
