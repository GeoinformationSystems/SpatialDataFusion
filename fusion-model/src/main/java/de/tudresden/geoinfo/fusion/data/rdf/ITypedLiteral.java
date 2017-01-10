package de.tudresden.geoinfo.fusion.data.rdf;

/**
 * RDF Typed Literal, literal with associated type identifier
 */
public interface ITypedLiteral extends ILiteral {
	
	/**
	 * get literal type identifier
	 * @return literal type identifier
	 */
    IResource getType();
	
}
