package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * RDF resource implementation
 */
public class Resource implements IResource {

    private IIdentifier identifier;

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    public Resource(@Nullable IIdentifier identifier) {
        this.identifier = identifier != null ? identifier : new Identifier();
    }

    /**
     * constructor
     *
     * @param sIdentifier resource identifier as string
     */
    public Resource(@NotNull String sIdentifier) {
        this(new Identifier(sIdentifier));
    }

    /**
     * constructor
     *
     */
    public Resource() {
        this(new Identifier());
    }

    @NotNull
    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isBlank() {
        //TODO: determine blank node that does not refer to an actual online resource
        return false;
    }

    @Override
    public boolean equals(@NotNull Object resource) {
        return resource instanceof IResource && this.getIdentifier().equals(((IResource) resource).getIdentifier());
    }

}
