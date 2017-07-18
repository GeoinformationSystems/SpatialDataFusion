package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Relation object, describes a qualified relationship between member resources
 *
 */
public interface IRelation extends IRDFResource {

    /**
     * get relation members
     *
     * @return relation members
     */
    @NotNull
    Set<IRDFResource> getMembers();

    /**
     * get relation member for particular role
     *
     * @param role relation role
     * @return relation members associated with role
     */
    @NotNull
    Set<IRDFResource> getMembers(@NotNull IRole role);

    /**
     * get relation type
     *
     * @return relation type
     */
    @NotNull
    IRelationType getRelationType();

}

