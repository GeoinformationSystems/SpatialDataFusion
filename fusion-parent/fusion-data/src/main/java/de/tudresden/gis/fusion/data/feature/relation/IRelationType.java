package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IIdentifiableObject;

public interface IRelationType extends IIdentifiableObject {

	/**
	 * get roles specified by this relation type
	 * @return relation roles
	 */
	public Collection<IRole> getRoles();
	
}
