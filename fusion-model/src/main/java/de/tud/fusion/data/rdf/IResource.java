package de.tud.fusion.data.rdf;

import java.net.URI;

import de.tud.fusion.data.IIdentifiableObject;

/**
 * RDF Resource, any identifiable object in the RDF graph (can be subject, predicate or object of a triple)
 * It is up to the implementation to decide, if an empty resource is allowed
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IResource extends INode,IIdentifiableObject {
	
	/**
	 * Java.net.URI representation of this resource
	 * @return resource URI
	 */
	public URI getURI();

	/**
	 * check if node is blank (identifier is blank or null)
	 * @return true, if node is a blank node
	 */
	public boolean isBlank();
	
}
