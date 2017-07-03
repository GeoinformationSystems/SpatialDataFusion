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
public class BooleanLiteral extends Measurement<Boolean> {

    private static IResource TYPE = Objects.BOOLEAN.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public BooleanLiteral(@Nullable IIdentifier identifier, boolean value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation) {
        super(identifier, value, metadata, TYPE, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value                literal value
     * @param measurementOperation associated measurement operation
     */
    public BooleanLiteral(boolean value, @Nullable IResource measurementOperation) {
        this(null, value, null, measurementOperation);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public BooleanLiteral(boolean value) {
        this(value, null);
    }

    /**
     * get maximum range for this literal type
     *
     * @return maximum range
     */
    @NotNull
    public static IMeasurementRange<Boolean> getMaxRange() {
        SortedSet<Boolean> range = new TreeSet<>();
        range.add(true);
        range.add(false);
        return new MeasurementRange<>(range, false);
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
