package de.tudresden.gis.fusion.data;

public interface IDataValue {

	/**
	 * get type of the data
	 * @return data type
	 */
	public IDataType getType();
	
	/**
	 * get content of the data value
	 * @return Java object representing the content of the data value
	 */
	public Object getContent();
	
}
