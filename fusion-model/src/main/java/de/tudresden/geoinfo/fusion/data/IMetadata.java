package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Metadata
 * TODO support additional metadata elements
 */
public interface IMetadata {

    /**
     * get title (http://purl.org/dc/terms/title)
     *
     * @return data title
     */
    @NotNull
    String getTitle();

    /**
     * get abstract description (http://purl.org/dc/terms/description)
     *
     * @return data description
     */
    @Nullable
    String getDescription();

}
