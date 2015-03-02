package de.tudresden.gis.fusion.metadata.data;

import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;

/**
 * basic description for a data object
 * @author Stefan
 *
 */
public interface IDescription {
	
	/**
	 * get abstract description for object
	 * @return abstract description
	 */
	public String getAbstract();
	
	/**
	 * get RDF representation of data object
	 * @return RDF representation of object
	 */
	public IRDFRepresentation getRDFRepresentation();

}
