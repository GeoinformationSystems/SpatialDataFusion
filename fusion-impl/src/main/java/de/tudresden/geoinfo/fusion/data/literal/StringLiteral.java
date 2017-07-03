package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * String literal implementation
 */
public class StringLiteral extends LiteralData<String> {

    private static IResource TYPE = Objects.STRING.getResource();

    /**
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public StringLiteral(@Nullable IIdentifier identifier, @NotNull String value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public StringLiteral(@NotNull String value) {
        this(null, value, null);
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

}
