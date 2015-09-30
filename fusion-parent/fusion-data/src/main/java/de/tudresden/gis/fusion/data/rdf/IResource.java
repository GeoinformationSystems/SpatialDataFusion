package de.tudresden.gis.fusion.data.rdf;

public interface IResource extends IIdentifiableResource {

	/**
	 * check if node is blank
	 * @return true, if node is blank node (identifier is blank)
	 */
	public boolean isBlank();
	
}
