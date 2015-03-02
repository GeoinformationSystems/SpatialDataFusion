package de.tudresden.gis.fusion.operation.io;

import de.tudresden.gis.fusion.data.IData;

/**
 * basic filter for complex data
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IFilter {
	
	/**
	 * filter input data
	 * @param input input
	 * @return filtered input
	 */
	public IData filter(IData input);

}
