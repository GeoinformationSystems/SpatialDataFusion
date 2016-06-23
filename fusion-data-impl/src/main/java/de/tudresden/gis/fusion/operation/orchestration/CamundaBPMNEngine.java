package de.tudresden.gis.fusion.operation.orchestration;

import java.util.Map;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.aggregate.IProcessEngine;
import de.tudresden.gis.fusion.operation.description.IOperationProfile;

/**
 * BPNM engine for Camunda BPMN model
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class CamundaBPMNEngine implements IProcessEngine<CamundaBPMNModel> {

	@Override
	public Map<String,IData> execute(Map<String,IData> input) {
		for(IData data : input.values()){
			if(data instanceof CamundaBPMNModel)
				return execute((CamundaBPMNModel) data);
		}
		throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Input does not contain a supported BPMN model");
	}

	@Override
	public Map<String,IData> execute(CamundaBPMNModel input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IOperationProfile profile() {
		// TODO Auto-generated method stub
		return null;
	}

}
