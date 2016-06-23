package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.rdf.IResource;

/**
 * relation type
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationType extends IResource {

	/**
	 * get role of the reference as specified by this relation type
	 * @return source role
	 */
	public IResource getSource();
	
	/**
	 * get role of the target as specified by this relation type
	 * @return source role
	 */
	public IResource getTarget();
	
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
	
}
