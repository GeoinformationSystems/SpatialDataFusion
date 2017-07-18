package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * abstract data object
 */
public class DataResource<T> extends Data<T> implements IRDFResource {

    private IRDFResource resource;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param object     data object
     * @param metadata   metadata object
     */
    public DataResource(@NotNull IIdentifier identifier, @NotNull T object, @Nullable IMetadata metadata) {
        super(identifier, object, metadata);
        this.resource = new ResourceIdentifier(identifier);
    }

    @Override
    public @Nullable String getIRI() {
        return this.resource.getIRI();
    }

    @Override
    public boolean isBlank() {
        return this.resource.isBlank();
    }

    @Override
    public @Nullable URI toURI() throws URISyntaxException {
        return this.resource.toURI();
    }

    @Override
    public @Nullable URL toURL() throws MalformedURLException {
        return this.resource.toURL();
    }

}
