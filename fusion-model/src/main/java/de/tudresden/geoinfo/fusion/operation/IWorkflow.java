package de.tudresden.geoinfo.fusion.operation;

import java.util.Collection;

/**
 * Operation workflow
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IWorkflow extends IWorkflowOperation {

	/**
	 * get operations in the workflow
	 * @return all operations in the workflow
	 */
    Collection<IWorkflowOperation> getWorkflowOperations();
	
}
