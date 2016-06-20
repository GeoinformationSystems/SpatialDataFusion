package de.tudresden.gis.fusion.data;

import java.util.Collection;

public interface IDataCollection<T extends IData> extends IData,Iterable<T> {
	
	/**
	 * get data collection
	 * @return data collection
	 */
	public Collection<T> resolve();
	
}