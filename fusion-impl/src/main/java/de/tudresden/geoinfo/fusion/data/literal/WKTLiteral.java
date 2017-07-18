package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WKT literal implementation
 */
public class WKTLiteral extends LiteralData<String> {

    public final static IRDFProperty TYPE = Objects.WKT_LITERAL.getResource();

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public WKTLiteral(@NotNull IIdentifier identifier, @NotNull String value, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public WKTLiteral(@NotNull String value) {
        this(new ResourceIdentifier(), value, null);
    }

    @Override
    public @Nullable String getLanguage() {
        return null;
    }
}
