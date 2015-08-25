package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IRelationType extends IRDFIdentifiableResource {

	/**
	 * get role of the source feature view as specified by this relation type
	 * @return source role
	 */
	public IRole sourceRole();
	
	/**
	 * get role of the target feature view as specified by this relation type
	 * @return source role
	 */
	public IRole targetRole();
	
	/**
	 * check if a relation type is symmetric
	 * @return true if relation type is symmetric
	 */
	public boolean symmetric();
	
	/**
	 * check if a relation type is transitive
	 * @return true if relation type is transitive
	 */
	public boolean transitive();
	
	/**
	 * check if a relation type is reflexive
	 * @return true if relation type is reflexive
	 */
	public boolean reflexive();
	
	/**
	 * get inverse relation types
	 * @return inverse relation types
	 */
	public Collection<IRelationType> inverseTypes();
	
	/**
	 * get disjoint relation types
	 * @return disjoint relation types
	 */
	public Collection<IRelationType> disjointTypes();
	
	/**
	 * get underlying measurements for this relation type
	 * @return underlying relation measurements
	 */
	public Collection<IRelationMeasurement<?>> measurements();
	
}
