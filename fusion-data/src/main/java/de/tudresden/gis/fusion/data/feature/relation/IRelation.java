package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Set;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * basic relation object
 * @author Stefan Wiemann, TU Dresden
 *
 * @param <S> reference object type
 * @param <T> target object type
 */
public interface IRelation<S,T> extends IData,IResource {

	/**
	 * get reference feature of the relation
	 * @return source feature
	 */
	public S getReference();
	
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
	public Set<? extends IRelationMeasurement> getRelationMeasurements();
	
	/**
	 * add a measurement to this relation
	 * @param measurement input measurement
	 */
	public <M extends IRelationMeasurement> void addMeasurement(M measurement);
	
}
