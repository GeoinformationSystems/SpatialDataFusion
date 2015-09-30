package de.tudresden.gis.fusion.operation.description;

import java.util.Map;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;

public interface IOperationProfile extends IIdentifiableResource {

	/**
	 * get input descriptions
	 * @return operation input descriptions
	 */
	public Map<String,IInputDescription> inputDescription();
	
	/**
	 * get output descriptions
	 * @return operation output descriptions
	 */
	public Map<String,IOutputDescription> outputDescription();
	
	/**
	 * get process description for this operation
	 * @return process description
	 */
	public IProcessDescription processDescription();
	
}
