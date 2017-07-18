package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.*;
import de.tudresden.geoinfo.fusion.data.metadata.MeasurementRange;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Boolean literal implementation
 */
public class TimeInstantLiteral extends LiteralData<Instant> {

    public final static IRDFProperty TYPE = Objects.TIME_INSTANT.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public TimeInstantLiteral(@NotNull IIdentifier identifier, Instant value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value                literal value
     */
    public TimeInstantLiteral(Instant value) {
        this(new ResourceIdentifier(), value, null);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Instant> getMaxRange() {
        SortedSet<Instant> range = new TreeSet<>();
        range.add(Instant.MIN);
        range.add(Instant.MAX);
        return new MeasurementRange<>(range, false);
    }

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
