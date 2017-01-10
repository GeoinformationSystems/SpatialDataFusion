package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.ISubject;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

import java.util.Set;

/**
 * Relation object, describes a qualified relationship between member resources
 * @param <T> Resource type participating in the relation
 */
public interface IRelation<T extends IResource> extends ISubject {

    /**
     * get relation members
     * @return relation members
     */
    Set<T> getMembers();

    /**
     * get relation member
     * @param role relation role
     * @return relation member
     */
    Set<T> getMember(IRole role);

    /**
     * get relation type
     * @return relation type
     */
    IRelationType getRelationType();

}

