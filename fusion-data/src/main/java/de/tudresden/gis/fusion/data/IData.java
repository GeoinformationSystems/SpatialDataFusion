package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IDataDescription;

public interface IData {
	
	/**
	 * get resource value
	 * @return Java object represented by this resource
	 */
	public Object resolve();
	
	/**
	 * get description of this resource
	 * @return resource description
	 */
	public IDataDescription getDescription();

}
