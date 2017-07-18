package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 *
 */
public interface IRDFProperty extends IRDFResource {

    @NotNull
    @Override
    String getIRI();

    /**
     * get triples for this predicate
     * @return triples
     */
    Set<? extends IRDFStatement> getTriples();

}
