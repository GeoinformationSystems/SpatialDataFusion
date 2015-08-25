package de.tudresden.gis.fusion.operation;

import java.util.Map;

import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;

public interface IOperationProfile {

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
