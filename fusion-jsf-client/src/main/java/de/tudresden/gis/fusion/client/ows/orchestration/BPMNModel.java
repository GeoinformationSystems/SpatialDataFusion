package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.DataInputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.DataOutputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.SourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.camunda.bpm.model.bpmn.instance.DataInput;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutput;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.InputSet;
import org.camunda.bpm.model.bpmn.instance.IoSpecification;
import org.camunda.bpm.model.bpmn.instance.OutputSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.tudresden.gis.fusion.client.ows.orchestration.IONode.NodeType;

/**
 * BPMN model for process chaining
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class BPMNModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String NAME_SUFFIX = "_";
	private static final String ID_SUFFIX = "_";
	/**
	 * BPMN model (org.camunda.bpm.model.bpmn.BpmnModelInstance)
	 */
	private BpmnModelInstance bpmnModel;

	/**
	 * construct BPMN model
	 * @param connectionHandler input connection handler
	 * @throws IOException
	 */
	public BPMNModel(ConnectionHandler connectionHandler) throws IOException {
		initModel(connectionHandler);
	}
	
	/**
	 * initialize model
	 * @param connectionHandler input connection handler
	 * @throws IOException
	 */
	private void initModel(ConnectionHandler connectionHandler) throws IOException {
		
		//check connection handler
		if(connectionHandler == null || !connectionHandler.isValid())
			throw new IOException("connection handler is null or not valid");
		
		//init empty model
		bpmnModel = createEmptyModel("http://tu-dresden.de/uw/geo/gis/fusion");
		
		//create process
		Process bpmnProcess = createBPMNProcess("wps_orchestration");
		
		//add tasks
		for(IOProcess ioProcess : connectionHandler.getProcessSequence()){
			ServiceTask task = createBPMNTask(ioProcess, connectionHandler.getConnections(ioProcess));
			bpmnProcess.addChildElement(task);
		}
		
		//add data object for each connection
		Map<String,SequenceFlow> sequenceFlows = new HashMap<String,SequenceFlow>();
		for(IOConnection connection : connectionHandler.getConnections()){
			//add data object
			bpmnProcess.addChildElement(createDataObject(connection));
			//add data object reference
			bpmnProcess.addChildElement(createDataObjectRef(connection));
			//add sequence flow
			SequenceFlow sequenceFlow = createSequenceFlow(connection);
			sequenceFlows.put(sequenceFlow.getId(), sequenceFlow);
		}
		
		//all all sequence flows
		for(SequenceFlow sequenceFlow : sequenceFlows.values()) {
			bpmnProcess.addChildElement(sequenceFlow);
		}
		
		//add process
		bpmnModel.getDefinitions().addChildElement(bpmnProcess);
		
	}

	/**
	 * create empty model with definitions
	 * @return model instance
	 */
	private BpmnModelInstance createEmptyModel(String namespace) {
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace(namespace);
		modelInstance.setDefinitions(definitions);
		return modelInstance;
	}

	/**
	 * create BPMN process from io process
	 * @param connections connectons for io process
	 * @param process io process
	 * @return BPMN process definition
	 */
	private Process createBPMNProcess(String processId) {		
		//create BPMN process
		Process bpmnProcess = bpmnModel.newInstance(Process.class);
		bpmnProcess.setId(processId);
		return bpmnProcess;
	}
	
	/**
	 * create BPMN task
	 * @param ioProcess io process
	 * @param ioConnections connections
	 * @return BPMN task
	 */
	private ServiceTask createBPMNTask(IOProcess ioProcess, Set<IOConnection> ioConnections) {
		
		//create BPMN process
		ServiceTask bpmnTask = bpmnModel.newInstance(ServiceTask.class);
		bpmnTask.setName(getBPMNTaskName(ioProcess));
		bpmnTask.setId(getBPMNTaskId(ioProcess));
		
		//create io specification
		bpmnTask.addChildElement(createIOSpecification(ioProcess.getNodes()));
		
		//create io associations
		for(IOConnection ioConnection : ioConnections){
			if(ioConnection.getStartProcess().equals(ioProcess))
				bpmnTask.addChildElement(createOutputAssociation(ioConnection));
			if(ioConnection.getEndProcess().equals(ioProcess))
				bpmnTask.addChildElement(createInputAssociation(ioConnection));
		}

		return bpmnTask;
	}

	private String getBPMNTaskName(IOProcess ioProcess){
		return escapeString(NAME_SUFFIX + ioProcess.getLocalIdentifier());
	}
	
	private String getBPMNTaskId(IOProcess ioProcess){
		return escapeString(ID_SUFFIX + ioProcess.getServiceIdentifier().hashCode() + getBPMNTaskName(ioProcess));
	}
	
	private String getBPMNTaskIdHash(IOProcess ioProcess){
		return escapeString(ID_SUFFIX + Math.abs(getBPMNTaskId(ioProcess).hashCode()));
	}

	/**
	 * create io specification for process
	 * @param ioNodes io nodes
	 * @return BPMN io specification
	 */
	private IoSpecification createIOSpecification(Set<IONode> ioNodes) {
		
		//create IoSpecification
		IoSpecification ioSpec = bpmnModel.newInstance(IoSpecification.class);
		
		//create data inputs and outputs
		for(IONode ioNode : ioNodes){
			if(ioNode.getType().equals(NodeType.INPUT) || ioNode.getType().equals(NodeType.BOTH))
				ioSpec.addChildElement(createDataInput(ioNode));
			if(ioNode.getType().equals(NodeType.OUTPUT) || ioNode.getType().equals(NodeType.BOTH))
				ioSpec.addChildElement(createDataOutput(ioNode));
		}
		
		//create input and output sets
		ioSpec.addChildElement(createInputSet(ioSpec.getDataInputs()));
		ioSpec.addChildElement(createOutputSet(ioSpec.getDataOutputs()));
		
		return ioSpec;
	}

	/**
	 * create BPMN data input
	 * @param node input node
	 * @param processId process id
	 * @return BPMN data input
	 */
	private DataInput createDataInput(IONode ioNode) {		
		DataInput input = bpmnModel.newInstance(DataInput.class);
		input.setName(getBPMNNodeName(ioNode));
		input.setId(getBPMNNodeId(ioNode));
		return input;
	}

	/**
	 * create BPMN data output
	 * @param node output node
	 * @param processId process id
	 * @return BPMN data output
	 */
	private DataOutput createDataOutput(IONode ioNode) {
		DataOutput output = bpmnModel.newInstance(DataOutput.class);
		output.setName(getBPMNNodeName(ioNode));
		output.setId(getBPMNNodeId(ioNode));
		return output;
	}
	
	private String getBPMNNodeName(IONode ioNode){
		return escapeString(NAME_SUFFIX + ioNode.getIdentifier());
	}
	
	private String getBPMNNodeId(IONode ioNode){
		return escapeString(ID_SUFFIX + getBPMNTaskId(ioNode.getProcess()) + getBPMNNodeName(ioNode));
	}

	/**
	 * create BPMN input set
	 * @param dataInputs BPMN data inputs
	 * @return BPMN input set
	 */
	private InputSet createInputSet(Collection<DataInput> dataInputs) {
		InputSet inputSet = bpmnModel.newInstance(InputSet.class);
		//add input references
		for(DataInput dataInput : dataInputs){
			DataInputRefs iRef = bpmnModel.newInstance(DataInputRefs.class);
			iRef.setTextContent(dataInput.getId());
		}
		return inputSet;
	}

	/**
	 * create BPMN output set
	 * @param dataOutputs BPMN data outputs
	 * @return BPMN output set
	 */
	private OutputSet createOutputSet(Collection<DataOutput> dataOutputs) {
		OutputSet outputSet = bpmnModel.newInstance(OutputSet.class);
		//add output references
		for(DataOutput dataOutput : dataOutputs){
			DataOutputRefs oRef = bpmnModel.newInstance(DataOutputRefs.class);
			oRef.setTextContent(dataOutput.getId());
		}
		return outputSet;
	}
	
	/**
	 * create input association
	 * @param connection io process connection
	 * @return input association for connection
	 */
	private ModelElementInstance createInputAssociation(IOConnection connection) {
		
		//create BPMN data input association
		DataInputAssociation inputAssociation = bpmnModel.newInstance(DataInputAssociation.class);		
		//set source reference
		SourceRef source = bpmnModel.newInstance(SourceRef.class);
		source.setTextContent(getAssociationId(connection));
		inputAssociation.addChildElement(source);
		//set target reference
		TargetRef target = bpmnModel.newInstance(TargetRef.class);
		target.setTextContent(getBPMNNodeId(connection.getEnd()));
		inputAssociation.addChildElement(target);
		
		return inputAssociation;
	}

	/**
	 * create output association
	 * @param connection io process connection
	 * @return output association for connection
	 */
	private ModelElementInstance createOutputAssociation(IOConnection connection) {

		//create BPMN data output association
		DataOutputAssociation outputAssociation = bpmnModel.newInstance(DataOutputAssociation.class);				
		//set source reference
		SourceRef source = bpmnModel.newInstance(SourceRef.class);
		source.setTextContent(getBPMNNodeId(connection.getEnd()));
		outputAssociation.addChildElement(source);
		//set target reference
		TargetRef target = bpmnModel.newInstance(TargetRef.class);
		target.setTextContent(getAssociationId(connection));
		outputAssociation.addChildElement(target);
		
		return outputAssociation;
	}
	
	/**
	 * get identifier for association of two ioNodes in a connection
	 * @param connection input connection
	 * @return association identifier
	 */
	private String getAssociationId(IOConnection connection){
		return escapeString(ID_SUFFIX + getBPMNNodeId(connection.getStart()) + "_" + getBPMNNodeId(connection.getEnd()));
	}
	
	private String getAssociationIdHash(IOConnection connection){
		return escapeString(ID_SUFFIX + Math.abs(getAssociationId(connection).hashCode()));
	}
	
	/**
	 * create data object for connection
	 * @param connection connection
	 * @return data object for connection
	 */
	private DataObject createDataObject(IOConnection connection) {
		DataObject dataObject = bpmnModel.newInstance(DataObject.class);
		dataObject.setId(getAssociationId(connection));
		return dataObject;
	}

	/**
	 * create data object reference for connection
	 * @param connection connection
	 * @return data object reference for connection
	 */
	private DataObjectReference createDataObjectRef(IOConnection connection) {
		DataObjectReference dataObjectRef = bpmnModel.newInstance(DataObjectReference.class);
		dataObjectRef.setId(getAssociationIdHash(connection));
		dataObjectRef.setAttributeValue("dataObjectRef", getAssociationId(connection));
		return dataObjectRef;
	}
	
	/**
	 * create sequence flow from io connection
	 * @param connection io connection
	 * @return sequence flow for connection
	 */
	private SequenceFlow createSequenceFlow(IOConnection connection) {
		SequenceFlow sequenceFlow = bpmnModel.newInstance(SequenceFlow.class);
		sequenceFlow.setId(ID_SUFFIX + getSequenceFlowId(connection));
		sequenceFlow.setAttributeValue("sourceRef", getBPMNTaskId(connection.getStart().getProcess()));
		sequenceFlow.setAttributeValue("targetRef", getBPMNTaskId(connection.getEnd().getProcess()));		
		return sequenceFlow;
	}
	
	private String getSequenceFlowId(IOConnection connection){
		return escapeString(getBPMNTaskIdHash(connection.getStart().getProcess()) + getBPMNTaskIdHash(connection.getEnd().getProcess()));
	}
	
	/**
	 * escape and normalize String to match NCName declaration
	 * @param string input string escaped string
	 * @return escaped and normalized string
	 */
	private String escapeString(String string){
		return string.replaceAll(":", "_").replaceAll("_+", "_");
	}
	
	/**
	 * get BPMN XML representation
	 * @return XML string
	 * @throws ModelValidationException
	 */
	public String asXML() throws IOException {
		try {
			return Bpmn.convertToString(bpmnModel);
		} catch (ModelValidationException mve) {
			throw new IOException("could not create model: " + mve.getLocalizedMessage());
		}
		
	}
	
}
