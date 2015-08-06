package de.tudresden.gis.fusion.data;


public interface IData {
	
	/**
	 * get identifier of this data object
	 * @return data identifier
	 */
	public IRI getIdentifier();
	
	/**
	 * get data value
	 * @return data value
	 */
	public IDataValue getValue();

}
