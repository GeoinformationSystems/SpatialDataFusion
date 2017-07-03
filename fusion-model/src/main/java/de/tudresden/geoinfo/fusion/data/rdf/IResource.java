package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;

/**
 * RDF Resource
 */
public interface IResource extends INode {

    /**
     * unique identifier for this resource
     *
     * @return resource identifier
     */
    @NotNull
    IIdentifier getIdentifier();

    /**
     * check, if resource is a blank node (identifier does not refer to an actual online resource)
     *
     * @return true, if node is blank
     */
    boolean isBlank();

}
