package de.tudresden.geoinfo.fusion.data.rdf;

/**
 * RDF Literal, literal object in the RDF graph (can only be object of a triple)
 */
public interface ILiteral extends INode {
	
	/**
	 * get value of this literal node
	 * @return node value
	 */
    String getValue();
	
}
