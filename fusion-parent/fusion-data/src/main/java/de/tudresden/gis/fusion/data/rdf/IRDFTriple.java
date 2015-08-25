package de.tudresden.gis.fusion.data.rdf;

public interface IRDFTriple extends IRDFRepresentation {

	/**
	 * get predicate for this triple
	 * @return triple predicate
	 */
	public IRDFIdentifiableResource predicate();
	
	/**
	 * get object for this triple
	 * @return triple object
	 */
	public IRDFNode object();
	
}
