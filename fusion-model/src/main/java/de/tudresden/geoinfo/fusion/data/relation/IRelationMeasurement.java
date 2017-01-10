package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.IMeasurementData;
import de.tudresden.geoinfo.fusion.data.ISubject;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * Relation measurement, describes a quantifiable relationship between resources
 * @param <T> Resource type participating in the relation measurement
 */
public interface IRelationMeasurement<T extends IResource> extends ISubject {

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
     * get relation measurement data
     * @return relation measurement data
     */
    IMeasurementData getMeasurement();
	
}
