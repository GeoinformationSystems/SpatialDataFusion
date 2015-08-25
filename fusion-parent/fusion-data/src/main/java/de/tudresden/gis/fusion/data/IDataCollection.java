package de.tudresden.gis.fusion.data;

import java.util.Collection;
import java.util.Iterator;

public interface IDataCollection<T extends IData> extends IData,Iterable<T> {
	
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
	 * add an object to this collection
	 * @param object object to be added
	 */
	public void add(T object);
	
	/**
	 * get iterator for this collection
	 * @return collection iterator
	 */
	public Iterator<T> iterator();
	
	/**
	 * get data element by id
	 * @param identifier element id
	 * @return data element with specified identifier, null if no element is associated with specified identifier
	 */
	public T elementById(IRI identifier);
	
}