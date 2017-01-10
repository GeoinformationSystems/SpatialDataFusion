package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class WorkflowOperation extends AbstractOperation implements IWorkflowOperation {

	private AbstractOperation operation;
	private Collection<IWorkflowOperation> ancestors;
	private Collection<IWorkflowOperation> successors;
	
	/**
	 * Constructor
	 * @param identifier workflow operation identifier
	 */
	protected WorkflowOperation(IIdentifier identifier){
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
	public Map<IIdentifier,IInputConnector> initInputConnectors() {
		return operation.initInputConnectors();
	}

	@Override
	public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
		return operation.initOutputConnectors();
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
