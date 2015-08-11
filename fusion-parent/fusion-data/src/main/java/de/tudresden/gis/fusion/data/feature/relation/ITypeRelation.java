package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Map;

import de.tudresden.gis.fusion.data.feature.IFeatureType;

public interface ITypeRelation extends IRelation {

	/**
	 * get all feature views participating in the relation
	 * @return feature views with associated role
	 */
	public Map<IFeatureType,IRole> getMembers();
	
}
