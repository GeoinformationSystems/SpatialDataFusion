package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 *
 */
public class ResourceIdentifier implements IIdentifier, IRDFResource {

    private String globalIdentifier, localIdentifier;
    private boolean blank;

    /**
     * constructor
     * @param globalIdentifier global identifier
     * @param localIdentifier local identifier
     */
    public ResourceIdentifier(@Nullable String globalIdentifier, @Nullable String localIdentifier) {
        this.blank = globalIdentifier == null;
        this.globalIdentifier = globalIdentifier != null ? globalIdentifier : UUID.randomUUID().toString();
        this.localIdentifier = localIdentifier != null ? localIdentifier : this.globalIdentifier;
    }

    /**
     * constructor
     * @param resource resource identifier
     */
    public ResourceIdentifier(@Nullable String resource) {
        this(resource, null);
    }

    /**
     * constructor
     * @param resource resource identifier
     */
    public ResourceIdentifier(@NotNull URL resource) {
        this(resource.toString(), null);
    }

    /**
     * constructor
     */
    public ResourceIdentifier(IIdentifier identifier) {
        this(identifier.getGlobalIdentifier(), identifier.getLocalIdentifier());
    }

    /**
     * constructor
     */
    public ResourceIdentifier() {
        this(null, null);
    }

    /**
     * get identifier
     * @return this as the implementing identifier instance
     */
    @NotNull
    public IIdentifier getIdentifier() {
        return this;
    }

    @Override
    public @NotNull String getGlobalIdentifier() {
        return globalIdentifier;
    }

    @Override
    public @NotNull String getLocalIdentifier() {
        return localIdentifier;
    }

    @Override
    public void setLocalIdentifier(@NotNull String localIdentifier) {
        this.localIdentifier = localIdentifier;
    }

    @Override
    public boolean globallyEquals(@NotNull IIdentifier object) {
        return this.getGlobalIdentifier().equals(object.getGlobalIdentifier());
    }

    @Override
    public boolean locallyEquals(@NotNull IIdentifier object) {
        return this.getLocalIdentifier().equals(object.getLocalIdentifier());
    }

    @Override
    public boolean equals(@NotNull Object object) {
        return object instanceof IIdentifier && this.globallyEquals((IIdentifier) object);
    }

    @Override
    public int hashCode(){
        return this.globalIdentifier.hashCode();
    }

    @Override
    public @Nullable String getIRI() {
        return this.isBlank() ? null : this.getGlobalIdentifier();
    }

    @Override
    public @Nullable URI toURI() throws URISyntaxException {
        return this.getIRI() != null ? new URI(this.getIRI()) : null;
    }

    @Override
    public @Nullable URL toURL() throws MalformedURLException {
        return this.getIRI() != null ? new URL(this.getIRI()) : null;
    }

    @Override
    public boolean isBlank() {
        return this.blank;
    }

}
