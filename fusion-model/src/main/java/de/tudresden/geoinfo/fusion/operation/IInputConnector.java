package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;

/**
 * Workflow input connection
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IInputConnector extends IConnector {

	/**
	 * get default for the input data object
	 * @return default data object, null if no default is specified
	 */
    IData getDefault();
	
}
