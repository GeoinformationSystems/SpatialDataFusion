package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Relation object, describes a qualified relationship between member resources
 *
 * @param <T> Resource type participating in the relation
 */
public interface IRelation<T extends IResource> extends IResource {

    /**
     * get relation members
     *
     * @return relation members
     */
    @NotNull
    Set<T> getMembers();

    /**
     * get relation member
     *
     * @param role relation role
     * @return relation members associated with role
     * @throws IllegalArgumentException if role is not associated to relation
     */
    @NotNull
    Set<T> getMembers(@NotNull IRole role);

    /**
     * get relation type
     *
     * @return relation type
     */
    @NotNull
    IRelationType getRelationType();

}

