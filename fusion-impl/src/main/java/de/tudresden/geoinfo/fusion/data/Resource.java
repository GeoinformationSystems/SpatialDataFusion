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
    private String title, description;

    /**
     * constructor
     *
     * @param identifier  resource identifier
     * @param title       resource title
     * @param description resource description
     */
    public Resource(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description) {
        this.identifier = identifier != null ? identifier : new Identifier();
        this.title = title != null ? title : this.identifier.toString();
        this.description = description;
    }

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    public Resource(@NotNull IIdentifier identifier) {
        this(identifier, null, null);
    }

    /**
     * constructor
     *
     * @param title resource title
     */
    public Resource(@NotNull String title) {
        this(null, title, null);
    }

    @Override
    public @NotNull IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getTitle() {
        return title;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

}
