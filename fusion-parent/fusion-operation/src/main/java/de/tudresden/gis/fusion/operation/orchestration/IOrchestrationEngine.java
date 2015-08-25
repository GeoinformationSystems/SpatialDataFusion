package de.tudresden.gis.fusion.operation.orchestration;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.IOperationInstance;

public interface IOrchestrationEngine<T extends IOrchestrationModel> extends IOperationInstance {
	
	/**
	 * executes an BPMN model
	 * @param T input model
	 * @return process outputs
	 */
	public Map<String,IData> execute(T input);

}
