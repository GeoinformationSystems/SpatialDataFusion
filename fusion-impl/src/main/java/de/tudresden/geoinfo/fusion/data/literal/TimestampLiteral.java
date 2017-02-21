package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Boolean literal implementation
 */
public class TimestampLiteral extends LiteralData<LocalDateTime> {

    private static IResource TYPE = Objects.TIME_INSTANT.getResource();

    /**
     * constructor
     *
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public TimestampLiteral(@Nullable IIdentifier identifier, @NotNull LocalDateTime value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param identifier  literal identifier
     * @param value       literal value
     * @param title       literal title
     * @param description literal description
     */
    public TimestampLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, @NotNull LocalDateTime value) {
        this(identifier, value, new Metadata(title, description));
    }

    /**
     * constructor, creates random identifier
     *
     * @param value TimestampLiteral value
     */
    public TimestampLiteral(@NotNull LocalDateTime value) {
        this(null, value, null);
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
