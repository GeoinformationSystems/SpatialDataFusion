package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * RDF graph, collection of RDF triples
 */
public interface IGraph extends ISubject {

    @NotNull
    Collection<? extends INode> getNodes();

}
