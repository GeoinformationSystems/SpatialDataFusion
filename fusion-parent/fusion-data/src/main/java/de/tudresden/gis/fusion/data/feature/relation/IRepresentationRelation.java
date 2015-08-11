package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Map;

import de.tudresden.gis.fusion.data.feature.IFeatureRepresentation;

public interface IRepresentationRelation extends IRelation {

	/**
	 * get all feature views participating in the relation
	 * @return feature views with associated role
	 */
	public Map<IFeatureRepresentation,IRole> getMembers();
	
}
