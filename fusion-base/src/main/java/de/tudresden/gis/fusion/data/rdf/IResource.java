package de.tudresden.gis.fusion.data.rdf;

/**
 * basic RDF resource
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IResource extends INode {

	/**
	 * check whether node is blank
	 * @return true, if node is blank node (identifier = null)
	 */
	public boolean isBlank();
	
	@Override
	public IIRI getIdentifier();
	
}
