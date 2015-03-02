package de.tudresden.gis.fusion.data;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

/**
 * feature relation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFeatureRelation extends IComplexData {

	/**
	 * get reference feature object
	 * @return reference feature object
	 */
	public IFeature getReference();
	
	/**
	 * get target feature object
	 * @return target feature object
	 */
	public IFeature getTarget();
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Collection<IRelationMeasurement> getMeasurements();
	
	/**
	 * add relation measurement to feature relation
	 * @param measurement measurement to be added
	 */
	public void addRelationMeasurement(IRelationMeasurement measurement);
	
	/**
	 * check if feature relation contains specified relation measurement
	 * @param classification relation measurement classification
	 * @return true if relation contains at least one measurement for specified classification 
	 */
	public boolean containsRelationMeasurement(IIdentifiableResource classification);
	
	/**
	 * get feature relations for specified relation measurement classification
	 * @param classification relation measurement classification
	 * @return list of measurements that match the specified classification
	 */
	public IRelationMeasurement getRelationMeasurement(IIdentifiableResource classification);
	
}
