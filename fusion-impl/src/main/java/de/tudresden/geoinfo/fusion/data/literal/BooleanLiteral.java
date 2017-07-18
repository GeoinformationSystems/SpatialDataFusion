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
public class BooleanLiteral extends LiteralData<Boolean> {

    public final static IRDFProperty TYPE = Objects.BOOLEAN.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public BooleanLiteral(@NotNull IIdentifier identifier, boolean value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value                literal value
     */
    public BooleanLiteral(boolean value) {
        this(new ResourceIdentifier(), value, null);
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

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
