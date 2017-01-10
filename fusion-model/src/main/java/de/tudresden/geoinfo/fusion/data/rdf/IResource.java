package de.tudresden.geoinfo.fusion.data.rdf;

/**
 * RDF Resource
 */
public interface IResource extends INode {
	
	/**
	 * identifier for this resource
	 * @return resource identifier
	 */
	IIdentifier getIdentifier();

	/**
	 * check, if resource is blank node
	 * @return true, if node is blank
	 */
	boolean isBlank();
	
}
