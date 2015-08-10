package de.tudresden.gis.fusion.registry.model;

import java.util.Set;

import de.tudresden.gis.fusion.registry.IObjectDescription;

public interface IDataDescription extends IObjectDescription {
	
	/**
	 * get data type descriptions
	 * @return Set of data type descriptions
	 */
	public Set<IDataTypeDescription> getDataTypeDescriptions(); 

}
