package de.tudresden.gis.fusion.data;

import java.util.Collection;

/**
 * collection of data objects
 * @author Stefan Wiemann, TU Dresden
 *
 * @param <T> data object types in the collection
 */
public interface IDataCollection<T extends IData> extends IData,Iterable<T> {
	
	/**
	 * get data collection
	 * @return data collection
	 */
	public Collection<T> resolve();
	
}