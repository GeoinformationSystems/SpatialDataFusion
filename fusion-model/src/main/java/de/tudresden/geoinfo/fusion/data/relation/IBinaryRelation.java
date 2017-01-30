package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;

import java.util.Set;

/**
 * binary relation
 * @param <T> Resource type participating in the relation
 */
public interface IBinaryRelation<T extends IResource> extends IRelation<T> {

    /**
     * get relation domain
     * @return relation domain resource
     */
    T getDomain();

    /**
     * get relation range
     * @return relation range resource
     */
    T getRange();

    /**
     * get relation measurements between the two resources
     * @return relation measurements
     */
    Set<IRelationMeasurement> getMeasurements();

    /**
     * add relation measurement
     * @param measurement relation measurement
     */
    void addMeasurement(IRelationMeasurement measurement);

    /**
     * add a set of relation measurements
     * @param measurement relation measurements
     */
    void addMeasurements(Set<IRelationMeasurement> measurement);

    @Override
    IBinaryRelationType getRelationType();

}