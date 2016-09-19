package de.tud.fusion.operation;

import java.util.Collection;

/**
 * Basic operation in a workflow
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IWorkflowOperation extends IOperation {
	
	/**
	 * get ancestor operations in the workflow
	 * @return all ancestor operations
	 */
	public Collection<IWorkflowOperation> getAncestors();
	
	/**
	 * get successor operations in the workflow
	 * @return all ancestor operations
	 */
	public Collection<IWorkflowOperation> getSuccessors();
	
}
