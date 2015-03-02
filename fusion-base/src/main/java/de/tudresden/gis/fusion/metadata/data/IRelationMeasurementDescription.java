package de.tudresden.gis.fusion.metadata.data;

import java.util.Set;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

/**
 * relation description
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IRelationMeasurementDescription extends IMeasurementDescription {
	
	/**
	 * get relation classification
	 * @return relation classification
	 */
	public Set<IIdentifiableResource> getRelationTypes();

}
