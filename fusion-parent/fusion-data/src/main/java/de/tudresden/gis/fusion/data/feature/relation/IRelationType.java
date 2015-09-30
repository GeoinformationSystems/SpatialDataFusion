package de.tudresden.gis.fusion.data.feature.relation;

import java.util.Collection;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public interface IRelationType extends IIdentifiableResource {

	/**
	 * get role of the source feature view as specified by this relation type
	 * @return source role
	 */
	public IRole getSource();
	
	/**
	 * get role of the target feature view as specified by this relation type
	 * @return source role
	 */
	public IRole getTarget();
	
	/**
	 * check if a relation type is symmetric
	 * @return true if relation type is symmetric
	 */
	public boolean isSymmetric();
	
	/**
	 * check if a relation type is transitive
	 * @return true if relation type is transitive
	 */
	public boolean isTransitive();
	
	/**
	 * check if a relation type is reflexive
	 * @return true if relation type is reflexive
	 */
	public boolean isReflexive();
	
	/**
	 * get inverse relation type
	 * @return inverse relation type, null if no inverse type exists
	 */
	public IRelationType getInverse();
	
	/**
	 * get disjoint relation types
	 * @return disjoint relation types, empty set if no disjoint types exist
	 */
	public Collection<IRelationType> getDisjoint();
	
}
