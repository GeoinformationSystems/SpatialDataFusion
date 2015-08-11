package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Map;

import de.tudresden.gis.fusion.data.feature.IFeatureInstance;

public interface IInstanceRelation extends IRelation {

	/**
	 * get all feature views participating in the relation
	 * @return feature views with associated role
	 */
	public Map<IFeatureInstance,IRole> getMembers();
	
}
