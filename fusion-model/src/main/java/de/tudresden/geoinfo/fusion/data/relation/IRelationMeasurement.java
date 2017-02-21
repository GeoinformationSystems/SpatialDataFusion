package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMeasurement;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

/**
 * Relation measurement, describes a quantifiable relationship between resources
 *
 * @param <T> Resource type participating in the relation measurement
 */
public interface IRelationMeasurement<T extends Comparable<T>> extends IMeasurement<T> {

    /**
     * get relation domain
     *
     * @return relation domain resource
     */
    @NotNull
    IResource getDomain();

    /**
     * get relation range
     *
     * @return relation range resource
     */
    @NotNull
    IResource getRange();

}
