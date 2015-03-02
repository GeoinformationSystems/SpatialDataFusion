package de.tudresden.gis.fusion.data;

import de.tudresden.gis.fusion.data.rdf.ILiteral;

/**
 * simple data object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface ISimpleData extends IData {

	@Override
	public ILiteral getRDFRepresentation();
	
}
