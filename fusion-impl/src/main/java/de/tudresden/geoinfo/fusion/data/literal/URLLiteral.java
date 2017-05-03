package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.Metadata;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URI literal implementation
 */
public class URLLiteral extends LiteralData<URL> {

    private static IResource TYPE = Objects.ANYURI.getResource();
    private IOFormat format;

    /**
     * constructor
     *
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public URLLiteral(@Nullable IIdentifier identifier, @NotNull URL value, @Nullable IMetadata metadata, @Nullable IOFormat format) {
        super(identifier, value, metadata, TYPE);
        this.format = format;
    }

    /**
     * constructor
     *
     * @param identifier  literal identifier
     * @param value       literal value
     * @param title       literal title
     * @param description literal description
     */
    public URLLiteral(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description, @NotNull URL value, @Nullable IOFormat format) {
        this(identifier, value, new Metadata(title, description), format);
    }

    /**
     * constructor, creates random identifier
     *
     * @param value URL value
     */
    public URLLiteral(URL value, @Nullable IOFormat format) {
        this(null, value, null, format);
    }

    /**
     * constructor, creates random identifier
     *
     * @param sValue URL value
     */
    public URLLiteral(String sValue, @Nullable IOFormat format) throws MalformedURLException {
        this(null, new URL(sValue), null, format);
    }

    /**
     * constructor, creates random identifier
     *
     * @param value URI value
     */
    public URLLiteral(@NotNull URL value) {
        this(value, null);
    }

    /**
     * constructor, creates random identifier
     *
     * @param sValue URL string value
     */
    public URLLiteral(@NotNull String sValue) throws MalformedURLException {
        this(sValue, null);
    }

    @Override
    public @NotNull IResource getLiteralType() {
        return TYPE;
    }

    /**
     * get base URL of the response(first part of URI string split by ?)
     *
     * @return base URL
     */
    public @NotNull URL getBase() {
        try {
            return new URL(this.resolve().toString().split("\\?")[0]);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not extract URL base");
        }
    }

    /**
     * get IO format associated with uri literal
     * @return IO format
     */
    public IOFormat getIOFormat(){
        return this.format;
    }

    /**
     * get RegEx for URL validation; !note: the RegEx is rather permissive!
     *
     * @return URL regex string
     */
    public static @NotNull String getURLRegex() {
        return "" +
                "(https?|file|ftp):" +    //scheme
                "//[^\\?#]*" +            //authority
                "[^\\?#]*" +            //path
                "(\\?[^#]*)?" +            //query
                "(#\\w*)?";                //fragment
    }

    @Override
    public String toString() {
        return this.resolve().toString();
    }

}
