package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Set;

public interface IRelation<T> {

	/**
	 * get reference feature of the relation
	 * @return source feature
	 */
	public T getSource();
	
	/**
	 * get target feature of the relation
	 * @return target feature
	 */
	public T getTarget();
	
	/**
	 * get relation type for this relation
	 * @return relation type
	 */
	public Set<IRelationType> getRelationTypes();
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Set<IRelationMeasurement> getRelationMeasurements();
	
	/**
	 * add a measurement to this relation
	 * @param measurement input measurement
	 */
	public void addMeasurement(IRelationMeasurement measurement);
	
}
