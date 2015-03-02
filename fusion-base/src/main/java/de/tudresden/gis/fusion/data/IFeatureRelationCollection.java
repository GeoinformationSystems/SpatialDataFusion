package de.tudresden.gis.fusion.data;

import java.util.Collection;

/**
 * collection of feature relations
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureRelationCollection extends IComplexData,Iterable<IFeatureRelation> {

	/**
	 * number of feature relations contained in selection
	 * @return size of collection
	 */
	public int size();
	
	/**
	 * get relation collection
	 * @return relations
	 */
	public Collection<IFeatureRelation> getRelations();
	
	/**
	 * add relation to relation collection
	 * @param relation relation to be added
	 */
	public void addRelation(IFeatureRelation relation);
	
}
