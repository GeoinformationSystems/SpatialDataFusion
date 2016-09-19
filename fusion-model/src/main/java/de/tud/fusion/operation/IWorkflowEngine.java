package de.tud.fusion.operation;

import java.util.Map;

import de.tud.fusion.data.IData;

/**
 * Workflow engine
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IWorkflowEngine extends IOperation {
	
	/**
	 * execute a workflow
	 * @param input input workflow
	 * @return process outputs
	 */
	public Map<String,IData> execute(IWorkflow input);

}
