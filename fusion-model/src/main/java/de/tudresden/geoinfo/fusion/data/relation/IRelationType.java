package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Relation type
 */
public interface IRelationType extends IResource {

    /**
     * get roles participating in this relation
     *
     * @return relation roles
     */
    @NotNull
    Collection<IRole> getRoles();

}
