package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;

/**
 * RDF Resource, any identifiable object in the RDF graph (can be subject, predicate or object of a triple)
 * It is up to the implementation to decide, if an empty resource is allowed
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IResource extends INode {
	
	/**
	 * String representation of this identifier
	 * @return identifier String
	 */
	public String getIdentifier();
	
	/**
	 * Java.net.URI representation of this identifier
	 * @return identifier URI
	 */
	public URI getURI();

	/**
	 * check if node is blank
	 * @return true, if node is a blank node (identifier is blank)
	 */
	public boolean isBlank();
	
}
