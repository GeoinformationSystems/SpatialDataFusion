package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;

public interface IRelation<T> extends IData,IRDFResource {
	
	/**
	 * get reference view of the relation
	 * @return reference feature view
	 */
	public T source();
	
	/**
	 * get target view of the relation
	 * @return target feature view
	 */
	public T target();

	/**
	 * get relation types for this relation
	 * @return relation types
	 */
	public Collection<IRelationType> relationTypes();
	
	/**
	 * add a relation type
	 * @param type relation type
	 */
	public void add(IRelationType type);
	
	/**
	 * get relation measurements for this relation
	 * @return relation measurements
	 */
	public Collection<IRelationMeasurement<?>> relationMeasurements();
	
	/**
	 * add a relation measurement
	 * @param measurement relation measurement
	 */
	public void add(IRelationMeasurement<?> measurement);
}
