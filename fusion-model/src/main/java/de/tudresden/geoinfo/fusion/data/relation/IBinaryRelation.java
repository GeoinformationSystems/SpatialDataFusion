package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * binary relation
 *
 */
public interface IBinaryRelation extends IRelation {

    /**
     * get relation domain
     *
     * @return relation domain resource
     */
    @NotNull
    IRDFResource getDomain();

    /**
     * get relation range
     *
     * @return relation range resource
     */
    @NotNull
    IRDFResource getRange();

    /**
     * get relation measurements between the two resources
     *
     * @return relation measurements
     */
    @NotNull
    Set<? extends IRelationMeasurement> getMeasurements();

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
