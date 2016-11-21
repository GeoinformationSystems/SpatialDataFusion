package de.tud.fusion.data.rdf;

import java.util.Collection;

/**
 * RDF graph, collection of RDF triples
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IGraph extends ISubject {

	/**
	 * get subjects associated with the graph
	 * @return all subjects of the graph
	 */
	public Collection<? extends ISubject> getSubjects();
	
}
