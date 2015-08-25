package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFResource;

public interface IRelationMeasurement<T extends Comparable<T>> extends IMeasurementValue<T>,IRDFResource {

	/**
	 * get reference for this relation measurement
	 * @return measurements reference
	 */
	public IRDFIdentifiableResource source();
	
	/**
	 * get target for this relation measurement
	 * @return measurements target
	 */
	public IRDFIdentifiableResource target();
	
}
