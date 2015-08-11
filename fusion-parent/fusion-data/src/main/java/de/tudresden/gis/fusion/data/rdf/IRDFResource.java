package de.tudresden.gis.fusion.data.rdf;

public interface IRDFResource extends IRDFIdentifiableResource {

	/**
	 * check whether node is blank
	 * @return true, if node is blank node (identifier = null)
	 */
	public boolean isBlank();
	
}