package de.tud.fusion.data.rdf;

import java.util.Set;

/**
 * RDF Subject, resource with associated properties (predicate - object pairs)
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ISubject extends IResource {
	
	/**
	 * get predicates associated with this subject
	 * @return predicate set for this subject
	 */
	public Set<IResource> getPredicates();
	
	/**
	 * get associated objects for specified predicate
	 * @param predicate input predicate
	 * @return object collection that is connected with predicate
	 * @throws IllegalArgumentException if predicate is not valid for this subject
	 */
	public Set<INode> getObjects(IResource predicate) throws IllegalArgumentException;
	
}
