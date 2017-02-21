package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * RDF Subject, resource with associated properties (predicate - object pairs)
 */
public interface ISubject extends IResource {

    /**
     * get predicates associated with this subject
     *
     * @return predicate set for this subject
     */
    @NotNull
    Set<IResource> getPredicates();

    /**
     * get associated objects for specified predicate
     *
     * @param predicate input predicate
     * @return object collection that is connected to predicate or null, if predicate is not valid for subject
     */
    @Nullable
    Set<INode> getObjects(@NotNull IResource predicate);

}
