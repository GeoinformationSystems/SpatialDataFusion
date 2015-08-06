package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.value.LiteralStringValue;

public interface IRDFPlainLiteral extends IRDFLiteral {

	@Override
	public LiteralStringValue getValue();
	
	/**
	 * get language identifier according to IANA Language Subtag Registry
	 * @return language identifier
	 */
	public String getLanguage();
	
}
