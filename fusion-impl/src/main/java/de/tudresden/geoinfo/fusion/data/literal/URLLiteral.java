package de.tudresden.geoinfo.fusion.data.literal;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URI literal implementation
 */
public class URLLiteral extends LiteralData<URL> {

    public final static IRDFProperty TYPE = Objects.ANYURI.getResource();
    private IOFormat format;

    /**
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public URLLiteral(@NotNull IIdentifier identifier, @NotNull URL value, @Nullable IMetadata metadata, @Nullable IOFormat format) {
        super(identifier, value, metadata, TYPE);
        this.format = format;
    }

    /**
     * constructor
     *
     * @param value literal value
     */
    public URLLiteral(@NotNull URL value, @Nullable IOFormat format) {
        this(new ResourceIdentifier(), value, null, format);
    }

    /**
     * constructor, creates random identifier
     *
     * @param sURL URL value
     */
    public URLLiteral(String sURL, @Nullable IOFormat format) {
        this(URLFromString(sURL), format);
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
    public URLLiteral(@NotNull String sValue) {
        this(sValue, null);
    }

    /**
     * create URL from String
     *
     * @param sURL input URL String
     * @return valid URL
     * @throws IllegalArgumentException, if sURL is not a valid URL
     */
    public static URL URLFromString(String sURL) {
        try {
            return new URL(sURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(sURL + " is not a valid URL");
        }
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
     * get IO format referenced by uri literal
     *
     * @return IO format
     */
    public IOFormat getIOFormat() {
        return this.format;
    }

    @Override
    public @Nullable String getLanguage() {
        return null;
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
                "[^\\?#]*" +               //path
                "(\\?[^#]*)?" +            //query
                "(#\\w*)?";                //fragment
    }

}
