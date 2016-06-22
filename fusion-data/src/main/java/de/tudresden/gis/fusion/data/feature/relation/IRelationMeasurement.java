package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public interface IRelationMeasurement extends IMeasurement,IIdentifiableResource {

	/**
	 * get reference for this relation measurement
	 * @return measurement reference
	 */
	public Object getSource();
	
	/**
	 * get target for this relation measurement
	 * @return measurement target
	 */
	public Object getTarget();
	
}
