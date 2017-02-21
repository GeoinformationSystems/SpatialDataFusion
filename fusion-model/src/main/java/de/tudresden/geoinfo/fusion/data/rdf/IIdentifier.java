package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * resource identifier
 */
public interface IIdentifier {

    /**
     * get global identifier
     *
     * @return global identifier
     */
    @NotNull
    URI getURI();

    /**
     * check identifier for equality
     *
     * @param identifier input identifier
     * @return true, if identifiers are equal
     */
    boolean equals(@NotNull IIdentifier identifier);

}
