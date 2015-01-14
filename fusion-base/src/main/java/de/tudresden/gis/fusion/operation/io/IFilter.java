package de.tudresden.gis.fusion.operation.io;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;

/**
 * basic filter for complex data
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFilter extends IRDFTripleSet {
	
	/**
	 * filter input data
	 * @param input input
	 * @return filtered input
	 */
	public IData filter(IData input);

}
