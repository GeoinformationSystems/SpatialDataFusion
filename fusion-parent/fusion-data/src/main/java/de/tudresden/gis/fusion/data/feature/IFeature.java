package de.tudresden.gis.fusion.data.feature;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;

public interface IFeature extends IData {
	
	/**
	 * get available views for this feature
	 * @return feature views
	 */
	public Collection<IFeatureView> getFeatureViews();
	
	/**
	 * get all relations associated with this feature
	 * @return feature relations
	 */
	public Collection<IRelation> getRelations();

}
