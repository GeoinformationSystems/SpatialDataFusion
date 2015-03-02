package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.metadata.data.IDescription;

/**
 * data binding
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IData {
	
	/**
	 * get RDF representation of data object
	 * @return RDF representation of object
	 */
	public INode getRDFRepresentation();

	/**
	 * get data description
	 * @return data description
	 */
	public IDescription getDescription();
	
}
