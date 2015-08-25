package de.tudresden.gis.fusion.data.rdf;

public interface IRDFPredicateObject {

	/**
	 * get predicate of this set
	 * @return predicate
	 */
	public IRDFIdentifiableResource predicate();
	
	/**
	 * get object of this set
	 * @return object
	 */
	public IRDFNode object();
	
}
