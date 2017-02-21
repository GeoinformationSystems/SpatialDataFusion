package de.tudresden.geoinfo.fusion.data.rdf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * get title of this resource (should be locally unique)
     *
     * @return resource title
     */
    @NotNull
    String getTitle();

    /**
     * get description of this resource
     *
     * @return resource description
     */
    @Nullable
    String getDescription();

    /**
     * check, if resource is blank node
     *
     * @return true, if node is blank
     */
    boolean isBlank();

}
