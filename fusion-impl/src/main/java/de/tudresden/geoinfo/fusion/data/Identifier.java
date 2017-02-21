package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 * RDF identifier implementation
 */
public class Identifier implements IIdentifier {

    private String sIdentifier;
    private transient URI uri;

    /**
     * constructor
     *
     * @param sIdentifier identifier
     */
    public Identifier(@NotNull String sIdentifier) {
        this.sIdentifier = sIdentifier;
    }

    /**
     * constructor
     *
     * @param uri uri
     */
    public Identifier(@NotNull URI uri) {
        this(uri.toString());
        this.uri = uri;
    }

    /**
     * constructor
     *
     * @param url url
     */
    public Identifier(@NotNull URL url) throws URISyntaxException {
        this(url.toURI());
    }

    /**
     * empty constructor, creates random identifiers
     */
    public Identifier() {
        this(UUID.randomUUID().toString());
    }

    @NotNull
    @Override
    public String toString() {
        return this.sIdentifier;
    }

    @Override
    public int hashCode() {
        return this.sIdentifier.hashCode();
    }

    @NotNull
    @Override
    public URI getURI() {
        if (this.uri == null)
            this.uri = URI.create(this.sIdentifier);
        return this.uri;
    }

    @Override
    public boolean equals(@NotNull IIdentifier identifier) {
        return this.sIdentifier.equals(identifier.toString());
    }
}
