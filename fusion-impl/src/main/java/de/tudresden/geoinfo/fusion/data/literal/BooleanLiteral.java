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
public class BooleanLiteral extends Measurement<Boolean> {

    private static IResource TYPE = Objects.BOOLEAN.getResource();

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                boolean value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public BooleanLiteral(@Nullable IIdentifier identifier, boolean value, @Nullable IMetadata metadata, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Boolean> measurementRange, @NotNull IResource uom) {
        super(identifier, value, metadata, TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier           data identifier
     * @param value                boolean value
     * @param measurementOperation associated measurement operation
     * @param measurementRange     associated measurement range
     * @param uom                  associated unit of measurement
     */
    public BooleanLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, boolean value, @Nullable IResource measurementOperation, @NotNull IMeasurementRange<Boolean> measurementRange, @NotNull IResource uom) {
        super(identifier, value, new Metadata(title, description), TYPE, measurementOperation, measurementRange, uom);
    }

    /**
     * constructor
     *
     * @param identifier data identifier
     * @param value      boolean value
     */
    public BooleanLiteral(@Nullable IIdentifier identifier, boolean value) {
        this(identifier, value, null, null, BooleanLiteral.getMaxRange(), Units.UNKNOWN.getResource());
    }

    /**
     * constructor, creates random identifier
     *
     * @param value boolean value
     */
    public BooleanLiteral(boolean value) {
        this(null, value);
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
