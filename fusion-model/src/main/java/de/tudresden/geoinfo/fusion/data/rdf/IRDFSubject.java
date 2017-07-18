package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * RDF Subject, resource with associated properties (predicate - object pairs)
 */
public interface IRDFSubject extends IRDFResource {

    /**
     * get resources associated with this node (RDF predicates)
     *
     * @return edges for this subject
     */
    @NotNull
    Set<IRDFProperty> getPredicates();

    /**
     * get all objects associated with specified RDF predicate
     *
     * @param predicate RDF predicate
     * @return RDF objects associated with specified predicate
     */
    @NotNull
    Set<IRDFNode> getObjects(@NotNull IRDFProperty predicate);

    /**
     * get degree of the node (number of connected edges)
     *
     * @return degree of the node
     */
    int getDegree();

}
