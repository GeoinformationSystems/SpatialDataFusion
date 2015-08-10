package de.tudresden.gis.fusion.registry.model;

import java.util.Set;

public interface IIODescription {
	
	/**
	 * get IO identifiers
	 * @return set of identifiers
	 */
	public Set<String> getIdentifiers();
	
	/**
	 * get IO description by identifier
	 * @param identifier IO description identifier
	 * @return data description associated with identifier, null if identifier does not exist
	 */
	public IDataDescription getDataDescription(String identifier);

}
