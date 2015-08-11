package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Map;

import de.tudresden.gis.fusion.data.feature.IFeatureConcept;

public interface IConceptRelation extends IRelation {

	/**
	 * get all feature views participating in the relation
	 * @return feature views with associated role
	 */
	public Map<IFeatureConcept,IRole> getMembers();
	
}
