package de.tudresden.gis.fusion.data.rdf;

import java.util.Collection;
import java.util.Set;

public interface ITripleSet extends ISubject {

	/**
	 * get predicates for this triple set
	 * @return triple predicate set
	 */
	public Collection<IIdentifiableResource> getPredicates();
	
	/**
	 * get objects for specified predicate
	 * @param predicate input predicate
	 * @return object collection that is connected with predicate
	 */
	public Set<INode> getObject(IIdentifiableResource predicate);
	
	/**
	 * get total size of this triple set (number of objects related to this set)
	 * @return size of triple set
	 */
	public int size();
	
}
