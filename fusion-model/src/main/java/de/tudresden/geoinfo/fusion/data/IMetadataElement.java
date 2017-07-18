package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface IMetadataElement {

    /**
     * get metadata resource (e.g. DC term)
     *
     * @return metadata resource
     */
    @NotNull
    IIdentifier getIdentifier();

    /**
     * get metadata value
     */
    @NotNull Object getValue();

}
