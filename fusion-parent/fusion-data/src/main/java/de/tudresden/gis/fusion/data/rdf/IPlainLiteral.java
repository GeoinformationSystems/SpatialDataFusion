package de.tudresden.gis.fusion.data.rdf;

public interface IPlainLiteral extends ILiteral {
	
	/**
	 * get language identifier according to IANA Language Subtag Registry
	 * @return language identifier
	 */
	public IIdentifiableResource getLanguage();
	
}
