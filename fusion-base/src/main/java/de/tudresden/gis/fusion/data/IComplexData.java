package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;

/**
 * complex data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IComplexData extends IData {
	
	@Override
	public IRDFRepresentation getRDFRepresentation();
	
}
