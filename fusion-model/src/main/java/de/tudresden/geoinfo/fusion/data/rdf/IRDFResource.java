package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 */
public interface IRDFResource extends IRDFNode {

    /**
     * get resource IRI
     *
     * @return resource IRI, null for blank resource
     */
    @Nullable
    String getIRI();

    /**
     * flag: resource is blank
     *
     * @return true, if this is a blank node (identifier equals null)
     */
    boolean isBlank();

    /**
     * get resource URI as String
     *
     * @return resource URI as String, null for blank resource
     * @throws URISyntaxException if the resource is not a valid Java URI
     */
    @Nullable
    URI toURI() throws URISyntaxException;

    /**
     * get resource URI as String
     *
     * @return resource URI as String, null for blank resource
     * @throws MalformedURLException if the resource is not a valid Java URL
     */
    @Nullable
    URL toURL() throws MalformedURLException;

}
