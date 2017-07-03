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

import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Boolean literal implementation
 */
public class TimeInstantLiteral extends Measurement<Instant> {

    private static IResource TYPE = Objects.TIME_INSTANT.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public TimeInstantLiteral(@Nullable IIdentifier identifier, Instant value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
    }

    /**
     * constructor
     *
     * @param title                literal title
     * @param value                literal value
     * @param measurementOperation associated measurement operation
     */
    public TimeInstantLiteral(@NotNull String title, Instant value, @Nullable IResource measurementOperation) {
        this(null, value, null, measurementOperation);
    }

    /**
     * constructor
     *
     * @param title literal title
     * @param value literal value
     */
    public TimeInstantLiteral(@NotNull String title, Instant value) {
        this(title, value, null);
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

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
