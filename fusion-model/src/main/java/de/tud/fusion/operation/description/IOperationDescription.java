package de.tud.fusion.operation.description;

import java.util.Set;

import de.tud.fusion.data.rdf.IResource;

public interface IOperationDescription extends IResource {

	/**
	 * get input connectors
	 * @return operation input connectors
	 */
	public Set<IInputConnector> getInputConnectors();
	
	/**
	 * get output connectors
	 * @return operation output connectors
	 */
	public Set<IOutputConnector> getOutputConnectors();
	
	/**
	 * get process description for this operation
	 * @return process description
	 */
	public IProcessDescription getProcessDescription();
	
}
