package de.tudresden.gis.fusion.data.rdf;

/**
 * RDF Typed Literal, literal with associated type identifier
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ITypedLiteral extends ILiteral {
	
	/**
	 * get literal type identifier
	 * @return literal type identifier
	 */
	public IResource getType();
	
}
