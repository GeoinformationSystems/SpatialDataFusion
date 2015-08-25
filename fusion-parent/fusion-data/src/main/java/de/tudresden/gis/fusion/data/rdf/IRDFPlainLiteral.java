package de.tudresden.gis.fusion.data.rdf;

public interface IRDFPlainLiteral extends IRDFLiteral {
	
	/**
	 * get language identifier according to IANA Language Subtag Registry
	 * @return language identifier
	 */
	public String language();
	
}
