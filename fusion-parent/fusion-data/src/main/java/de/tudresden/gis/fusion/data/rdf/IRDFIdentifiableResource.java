package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.IRI;

public interface IRDFIdentifiableResource extends IRDFNode {
	
	/**
	 * get identifier of this object
	 * @return object identifier
	 */
	public IRI identifier();
	
}
