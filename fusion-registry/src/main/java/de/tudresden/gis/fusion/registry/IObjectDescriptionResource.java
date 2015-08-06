package de.tudresden.gis.fusion.registry;

import de.tudresden.gis.fusion.data.rdf.IIRI;

public interface IObjectDescriptionResource extends IObjectDescription {

	/**
	 * get local identifier for this object description
	 * @return local identifier
	 */
	public String getLocalIdentifier();
	
	/**
	 * get unique identifier for this object description
	 * @return unique identifier
	 */
	public IIRI getIdentifier();
	
}
