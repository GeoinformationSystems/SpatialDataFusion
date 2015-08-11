package de.tudresden.gis.fusion.operation;

import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.constraint.IConstraint;

public interface IOperationInstance {

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
	public IOperationProfile getProfile();
	
	/**
	 * get constraints for this operation instance
	 * @return operation constraints
	 */
	public Collection<IConstraint> getConstraints();
	
}
