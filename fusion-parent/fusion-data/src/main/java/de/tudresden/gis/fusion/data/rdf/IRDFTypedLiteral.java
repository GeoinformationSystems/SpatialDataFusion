package de.tudresden.gis.fusion.data.rdf;

public interface IRDFTypedLiteral extends IRDFLiteral {
	
	/**
	 * get literal type
	 * @return literal type
	 */
	public IRDFIdentifiableResource type();
	
}
