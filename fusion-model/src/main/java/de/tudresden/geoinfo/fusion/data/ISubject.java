package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.INode;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

import java.util.Set;

/**
 * RDF Subject, resource with associated properties (predicate - object pairs)
 */
public interface ISubject extends IResource,IData {
	
	/**
	 * get predicates associated with this subject
	 * @return predicate set for this subject
	 */
    Set<IResource> getPredicates();
	
	/**
	 * get associated objects for specified predicate
	 * @param predicate input predicate
	 * @return object collection that is connected with predicate
	 */
    Set<INode> getObjects(IResource predicate);
	
}
