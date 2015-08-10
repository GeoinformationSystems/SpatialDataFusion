package de.tudresden.gis.fusion.data.rdf;

public interface IRDFTriple {

	/**
	 * get predicate for this triple
	 * @return triple predicate
	 */
	public IRDFIdentifiableResource getPredicate();
	
	/**
	 * get object for this triple
	 * @return triple object
	 */
	public IRDFNode getObject();
	
}
