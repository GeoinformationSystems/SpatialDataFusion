package de.tud.fusion.operation.description;

import java.util.Set;

import de.tud.fusion.data.rdf.Resource;

/**
 * Operation description instance
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OperationDescription extends Resource implements IOperationDescription {
	
	Set<IInputConnector> inputConnectors; 
	Set<IOutputConnector> outputConnectors; 
	IProcessDescription processDescription;

	/**
	 * constructor
	 * @param identifier operation identifier
	 * @param inputConnectors operation inputs
	 * @param outputConnectors operation outputs
	 * @param processDescription operation process description
	 */
	public OperationDescription(String identifier, Set<IInputConnector> inputConnectors, Set<IOutputConnector> outputConnectors, IProcessDescription processDescription) {
		super(identifier);
		this.inputConnectors = inputConnectors;
		this.outputConnectors = outputConnectors;
		this.processDescription = processDescription;
	}

	@Override
	public Set<IInputConnector> getInputConnectors() {
		return inputConnectors;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		return outputConnectors;
	}

	@Override
	public IProcessDescription getProcessDescription() {
		return processDescription;
	}

}
