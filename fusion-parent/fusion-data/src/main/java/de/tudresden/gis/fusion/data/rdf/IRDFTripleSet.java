package de.tudresden.gis.fusion.data.rdf;

import java.util.Collection;

public interface IRDFTripleSet extends IRDFRepresentation {

	/**
	 * get object set for this triple
	 * @return set of objects
	 */
	public Collection<IRDFPredicateObject> objectSet();
	
}
