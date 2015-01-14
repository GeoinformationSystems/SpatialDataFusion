package de.tudresden.gis.fusion.operation.io;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

/**
 * restrictions for data
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IDataRestriction extends IRDFTripleSet {

	/**
	 * check whether input data complies with restriction
	 * @param input input data
	 * @return true, if restriction is met by input data
	 */
	public boolean compliantWith(IData input);
	
}
