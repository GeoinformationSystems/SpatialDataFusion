package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WKT literal implementation
 */
public class WKTLiteral extends LiteralData<String> {

    private static IResource TYPE = Objects.WKT_LITERAL.getResource();

    /**
     * constructor
     *
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public WKTLiteral(@Nullable IIdentifier identifier, @NotNull String value, @Nullable IMetadata metadata) {
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
    public WKTLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, @NotNull String value) {
        this(identifier, value, new Metadata(title, description));
    }

    /**
     * constructor, creates random identifier
     *
     * @param value integer value
     */
    public WKTLiteral(@NotNull String value) {
        this(null, value, null);
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
