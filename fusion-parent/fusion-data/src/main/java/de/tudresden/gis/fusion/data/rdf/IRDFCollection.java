package de.tudresden.gis.fusion.data.rdf;

import java.util.Collection;

public interface IRDFCollection {
	
	/**
	 * get collection of RDF representations
	 * @return RDF collection
	 */
	public Collection<? extends IRDFRepresentation> rdfCollection();

}
