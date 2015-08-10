package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.description.IDataDescription;

public interface IData extends IIdentifiableObject {
	
	/**
	 * get data value
	 * @return Java object representing the data value
	 */
	public Object getValue();
	
	/**
	 * get description for data object
	 * @return data description
	 */
	public IDataDescription getDescription();

}
