package de.tudresden.gis.fusion.operation.aggregate;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.IOperation;

public interface IProcessEngine<T extends IProcessModel> extends IOperation {
	
	/**
	 * executes an BPMN model
	 * @param T input model
	 * @return process outputs
	 */
	public Map<String,IData> execute(T input);

}
