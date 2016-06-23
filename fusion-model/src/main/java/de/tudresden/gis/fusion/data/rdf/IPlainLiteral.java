package de.tudresden.gis.fusion.data.rdf;

/**
 * RDF Plain Literal, literal with associated language identifier
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IPlainLiteral extends ILiteral {
	
	/**
	 * get language identifier according to IANA Language Subtag Registry
	 * @return language identifier
	 */
	public IResource getLanguage();
	
}
