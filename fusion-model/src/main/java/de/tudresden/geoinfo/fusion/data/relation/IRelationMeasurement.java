package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMeasurement;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;

/**
 * Relation measurement, describes a quantifiable relationship between two objects
 *
 * @param <T> Measurement type
 */
public interface IRelationMeasurement<T extends Comparable<T>> extends IMeasurement<T>, IRDFResource {

    /**
     * get domain of this measurement
     *
     * @return domain resource
     */
    @NotNull
    IRDFResource getDomain();

    /**
     * get range of this measurement
     *
     * @return range resource
     */
    @NotNull
    IRDFResource getRange();

}
