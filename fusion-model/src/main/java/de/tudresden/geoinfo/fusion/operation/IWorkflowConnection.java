package de.tudresden.geoinfo.fusion.operation;

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
    IInputConnector getInput();
	
	/**
	 * get output connection
	 * @return output connection
	 */
    IOutputConnector getOutput();

}
