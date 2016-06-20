package de.tudresden.gis.fusion.data.rdf;

public interface ITriple extends ISubject {

	/**
	 * get predicate for this triple
	 * @return triple predicate
	 */
	public IIdentifiableResource getPredicate();
	
	/**
	 * get object for this triple
	 * @return triple object
	 */
	public INode getObject();
	
}
