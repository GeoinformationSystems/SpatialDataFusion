package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

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
	 * @param identifier workflow identifier
	 */
	public Workflow(IIdentifier identifier) {
		super(identifier);
	}
	
	/**
	 * Constructor
	 * @param identifer workflow identifier
	 * @param operations workflow operations
	 */
	public Workflow(IIdentifier identifer, Collection<IWorkflowOperation> operations) {
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
