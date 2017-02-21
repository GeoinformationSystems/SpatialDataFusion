package de.tudresden.geoinfo.fusion.data;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * collection of data objects
 */
public interface IDataCollection<T extends IData> extends IData, Iterable<T> {

    @Override
    @NotNull
    Collection<T> resolve();

}