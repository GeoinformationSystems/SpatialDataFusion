package de.tudresden.gis.fusion.data;

import java.util.Collection;

public interface IDataCollection<T extends IData> extends IData {
	
	/**
	 * get size of the collection
	 * @return collection size
	 */
	public int size();
	
	/**
	 * get data collection
	 * @return data collection
	 */
	public Collection<T> collection();
	
	/**
	 * get data element by id
	 * @param identifier element id
	 * @return data element with specified identifier, null if no element is associated with specified identifier
	 */
	public T getElement(IRI identifier);
	
}