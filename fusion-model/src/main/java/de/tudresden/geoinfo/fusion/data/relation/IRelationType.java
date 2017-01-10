package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.ISubject;

import java.util.Collection;

/**
 * Relation type
 */
public interface IRelationType extends ISubject {

    /**
     * get roles participating in this relation
     * @return relation roles
     */
    Collection<IRole> getRoles();
	
}
