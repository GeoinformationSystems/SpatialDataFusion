package de.tud.fusion.operation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * standard workflow operation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Workflow extends WorkflowOperation implements IWorkflow {

	private Collection<IWorkflowOperation> operations;
	
	/**
	 * Constructor
	 * @param identifer workflow identifier
	 */
	public Workflow(String identifer) {
		super(identifer);
	}
	
	/**
	 * Constructor
	 * @param identifer workflow identifier
	 * @param operations workflow operations
	 */
	public Workflow(String identifer, Collection<IWorkflowOperation> operations) {
		this(identifer);
		this.operations = operations;
	}
	
	/**
	 * add workflow operation
	 * @param operation input operation
	 */
	public void addOperation(IWorkflowOperation operation){
		if(operations == null)
			operations = new ArrayList<IWorkflowOperation>();
		operations.add(operation);
	}

	@Override
	public Collection<IWorkflowOperation> getWorkflowOperations() {
		return operations;
	}

}
