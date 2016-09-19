package de.tud.fusion.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;

public class WorkflowOperation extends AbstractOperation implements IWorkflowOperation {

	private AbstractOperation operation;
	Collection<IWorkflowOperation> ancestors;
	Collection<IWorkflowOperation> successors;
	
	/**
	 * Constructor
	 * @param identifier workflow operation identifier
	 */
	protected WorkflowOperation(String identifier){
		super(identifier);
	}
	
	/**
	 * Constructor
	 * @param operation input operation
	 */
	public WorkflowOperation(AbstractOperation operation) {
		this(operation.getIdentifier());
		this.operation = operation;
		this.ancestors = new ArrayList<IWorkflowOperation>();
		this.successors = new ArrayList<IWorkflowOperation>();
	}

	@Override
	public Set<IInputConnector> getInputConnectors() {
		return operation.getInputConnectors();
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		return operation.getOutputConnectors();
	}

	@Override
	public void execute() {
		operation.execute();
	}

	@Override
	public String getProcessTitle() {
		return operation.getProcessTitle();
	}

	@Override
	public String getProcessAbstract() {
		return operation.getProcessAbstract();
	}

	@Override
	public Collection<IWorkflowOperation> getAncestors() {
		return ancestors;
	}

	@Override
	public Collection<IWorkflowOperation> getSuccessors() {
		return successors;
	}
	
	/**
	 * add ancestor in workflow
	 * @param ancestor workflow ancestor
	 */
	public void addAncestor(IWorkflowOperation ancestor){
		ancestors.add(ancestor);
	}
	
	/**
	 * add successor in workflow
	 * @param successor workflow successor
	 */
	public void addSuccessors(IWorkflowOperation successor){
		ancestors.add(successor);
	}

}
