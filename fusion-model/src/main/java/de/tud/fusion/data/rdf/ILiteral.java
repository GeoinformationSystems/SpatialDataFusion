package de.tud.fusion.data.rdf;

/**
 * RDF Literal, literal object in the RDF graph (can only be object of a triple)
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ILiteral extends INode {
	
	/**
	 * get value of this literal node
	 * @return node value
	 */
	public String getValue();
	
}
