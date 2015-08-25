package de.tudresden.gis.fusion.operation.orchestration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.ModelValidationException;

import de.tudresden.gis.fusion.data.description.IDataDescription;
import de.tudresden.gis.fusion.operation.IOperationInstance;
import de.tudresden.gis.fusion.operation.orchestration.IOrchestrationModel;

public class CamundaBPMNModel implements IOrchestrationModel {

	private BpmnModelInstance model;
	
	public CamundaBPMNModel(BpmnModelInstance model){
		this.model = model;
	}
	
	@Override
	public Object value() {
		return model;
	}

	@Override
	public IDataDescription description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<IOperationInstance> getOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * get XML input stream following BPMN 2.0 schema
	 * @return XML input stream
	 * @throws ModelValidationException
	 */
	public InputStream asXML() throws ModelValidationException {
		return new ByteArrayInputStream(Bpmn.convertToString(model).getBytes(StandardCharsets.UTF_8));
	}

}
