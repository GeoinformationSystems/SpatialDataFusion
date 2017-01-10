package de.tudresden.geoinfo.fusion.data;

import java.util.Collection;

/**
 * collection of data objects
 */
public interface IDataCollection<T extends IData> extends IData,Iterable<T> {
	
	@Override
    Collection<T> resolve();
	
}