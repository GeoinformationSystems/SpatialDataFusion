package de.tud.fusion.data.relation;

import java.util.Set;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.rdf.IResource;

/**
 * basic relation object
 * @author Stefan Wiemann, TU Dresden
 *
 * @param <S> reference object type
 * @param <T> target object type
 */
public interface IRelation<R,T> extends IResource,IData {

	/**
	 * get reference feature of the relation
	 * @return source feature
	 */
	public R getReference();
	
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
	
}
