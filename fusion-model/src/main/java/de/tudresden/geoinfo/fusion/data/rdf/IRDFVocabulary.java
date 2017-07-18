package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

/**
 * RDF vocabulary template
 */
public interface IRDFVocabulary {

    /**
     * get RDF resource
     *
     * @return RDF resource
     */
    @NotNull
    IRDFResource getResource();

}
