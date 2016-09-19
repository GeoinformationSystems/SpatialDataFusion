package de.tud.fusion.operation.description;

import de.tud.fusion.data.IData;

/**
 * Workflow input connection
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IInputConnector extends IIOConnector {

	/**
	 * get default for the input data object
	 * @return default data object, null if no default is specified
	 */
	public IData getDefault();
	
}
