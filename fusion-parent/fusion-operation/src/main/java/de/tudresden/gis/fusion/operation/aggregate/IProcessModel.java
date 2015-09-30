package de.tudresden.gis.fusion.operation.aggregate;

import java.util.LinkedList;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.IOperationInstance;

public interface IProcessModel extends IData {

	/**
	 * get list of operations in execution order
	 * @return list of operations
	 */
	public LinkedList<IOperationInstance> getOperations();
	
}
