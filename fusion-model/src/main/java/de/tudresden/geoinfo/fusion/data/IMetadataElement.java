package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
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
    IResource getResource();

    /**
     * get metadata value
     */
    @NotNull Object getValue();

}
