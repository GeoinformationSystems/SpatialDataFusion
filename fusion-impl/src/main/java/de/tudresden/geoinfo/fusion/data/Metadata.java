package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic metadata implementation
 */
public class Metadata implements IMetadata {

    private String title, description;

    public Metadata(@NotNull String title, @Nullable String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public @NotNull String getTitle() {
        return title;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

}
