package de.tudresden.gis.fusion.operation;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;

public interface IOperation {

	/**
	 * executes an operation
	 * @param input input parameters used to execute the operation
	 * @return operation output
	 */
	public Map<String,IData> execute(Map<String,IData> input);
	
	/**
	 * returns operation description profile
	 * @return operation profile
	 */
	public IOperationProfile profile();
	
}
