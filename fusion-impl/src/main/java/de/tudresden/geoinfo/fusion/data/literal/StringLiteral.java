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
 * String literal implementation
 */
public class StringLiteral extends LiteralData<String> {

    public final static IRDFProperty TYPE = Objects.STRING.getResource();
    private String language;

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param language literal language
     * @param metadata   literal metadata
     */
    public StringLiteral(@NotNull IIdentifier identifier, @NotNull String value, @Nullable String language, @Nullable IMetadata metadata) {
        super(identifier, value, metadata, TYPE);
        this.language = language != null ? language : "en";
    }

    /**
     * constructor
     *
     * @param value literal value
     * @param language literal language
     *
     */
    public StringLiteral(@NotNull String value, @Nullable String language) {
        this(new ResourceIdentifier(), value, language, null);
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public StringLiteral(@NotNull String value) {
        this(value, null);
    }

    @Override
    public @Nullable String getLanguage() {
        return this.language;
    }

}
