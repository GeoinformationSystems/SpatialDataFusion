package de.tud.fusion.operation;

import java.util.Map;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.IIdentifiableObject;
import de.tud.fusion.operation.description.IOperationDescription;

/**
 * Basic operation object
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IOperation extends IIdentifiableObject {

	/**
	 * executes an operation
	 * @param input input parameters used to execute the operation
	 * @return operation output
	 */
	public Map<String,IData> execute(Map<String,IData> input);
	
	/**
	 * returns operation description
	 * @return operation description
	 */
	public IOperationDescription getDescription();
	
}
