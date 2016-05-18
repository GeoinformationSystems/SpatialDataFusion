package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;

public interface IResource extends INode {

	/**
	 * check if node is blank
	 * @return true, if node is blank node (identifier is blank)
	 */
	public boolean isBlank();
	
	/**
	 * Java.net.URI representation of this identifier
	 * @return identifier URI
	 */
	public URI asURI();
	
	/**
	 * relativize a URI
	 * @param uri base URI
	 * @return relativized URI
	 */
	public URI relativizeURI(URI uri);
	
	/**
	 * String representation of this identifier
	 * @return identifier String
	 */
	public String identifier();
	
}
