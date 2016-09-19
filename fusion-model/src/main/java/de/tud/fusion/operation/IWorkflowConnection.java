package de.tud.fusion.operation;

import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;

/**
 * IO connection in a workflow
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IWorkflowConnection {
	
	/**
	 * get input connection
	 * @return input connection
	 */
	public IInputConnector getInput();
	
	/**
	 * get output connection
	 * @return output connection
	 */
	public IOutputConnector getOutput();

}
