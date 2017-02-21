package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * URI literal implementation
 */
public class URILiteral extends LiteralData<URI> {

    private static IResource TYPE = Objects.ANYURI.getResource();

    /**
     * constructor
     *
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public URILiteral(@Nullable IIdentifier identifier, @NotNull URI value, @Nullable IMetadata metadata) {
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
    public URILiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, @NotNull URI value) {
        this(identifier, value, new Metadata(title, description));
    }

    /**
     * constructor, creates random identifier
     *
     * @param value URI value
     */
    public URILiteral(@NotNull URI value) {
        this(null, value, null);
    }

    /**
     * get RegEx for URL validation; !note: the RegEx is rather permissive!
     *
     * @return URL regex string
     */
    @NotNull
    public static String getURLRegex() {
        return "" +
                "(https?|file|ftp):" +    //scheme
                "//[^\\?#]*" +            //authority
                "[^\\?#]*" +            //path
                "(\\?[^#]*)?" +            //query
                "(#\\w*)?";                //fragment
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return TYPE;
    }

    /**
     * get base URL of the response(first part of URI string split by ?)
     *
     * @return base URL
     */
    public URL getBaseURL() throws MalformedURLException {
        return new URL(this.resolve().toString().split("\\?")[0]);
    }

}
