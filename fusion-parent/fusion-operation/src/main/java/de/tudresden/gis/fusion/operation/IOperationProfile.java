package de.tudresden.gis.fusion.operation;

import java.util.Map;

import de.tudresden.gis.fusion.operation.description.IIODataDescription;
import de.tudresden.gis.fusion.operation.description.IProcessDescription;

public interface IOperationProfile {

	/**
	 * get input descriptions
	 * @return operation input descriptions
	 */
	public Map<String,IIODataDescription> getInputDescription();
	
	/**
	 * get output descriptions
	 * @return operation output descriptions
	 */
	public Map<String,IIODataDescription> getOutputDescription();
	
	/**
	 * get process description for this operation
	 * @return process description
	 */
	public IProcessDescription getProcessDescription();
	
}
