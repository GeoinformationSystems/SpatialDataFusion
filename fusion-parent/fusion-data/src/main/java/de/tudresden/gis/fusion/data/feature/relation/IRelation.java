package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.feature.IFeatureView;

public interface IRelation {
	
	/**
	 * get all feature views participating in the relation
	 * @return feature views with associated role
	 */
	public Map<IFeatureView,IRole> getMembers();

	/**
	 * get relation types for this relation
	 * @return relation types
	 */
	public Collection<IRelationType> getRelationTypes();
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Collection<IRelationMeasurement<?>> getRelationMeasurements();
	
}
