package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * binary relation
 *
 * @param <T> Resource type participating in the relation
 */
public interface IBinaryRelation<T extends IResource> extends IRelation<T> {

    /**
     * get relation domain
     *
     * @return relation domain resource
     */
    @NotNull
    T getDomain();

    /**
     * get relation range
     *
     * @return relation range resource
     */
    @NotNull
    T getRange();

    /**
     * get relation measurements between the two resources
     *
     * @return relation measurements
     */
    @NotNull
    Set<IRelationMeasurement> getMeasurements();

    /**
     * add relation measurement
     *
     * @param measurement relation measurement
     */
    void addMeasurement(@NotNull IRelationMeasurement measurement);

    @NotNull
    @Override
    IBinaryRelationType getRelationType();

}
