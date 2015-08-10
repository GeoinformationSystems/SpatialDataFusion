package de.tudresden.gis.fusion.data.rdf;

import java.util.Map;
import java.util.Set;

public interface IRDFTripleSet {

	/**
	 * get object set for this triple
	 * @return set of objects
	 */
	public Map<IRDFIdentifiableResource,Set<IRDFNode>> getObjectSet();
	
}
