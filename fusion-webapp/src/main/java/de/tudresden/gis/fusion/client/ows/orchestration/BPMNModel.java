package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.DataInputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.DataOutputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.impl.instance.SourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.camunda.bpm.model.bpmn.instance.DataInput;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutput;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.InputSet;
import org.camunda.bpm.model.bpmn.instance.IoSpecification;
import org.camunda.bpm.model.bpmn.instance.OutputSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
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
	
	private final String NAME_SUFFIX = "_";
	private final String ID_SUFFIX = "_";
	private final String ID_SEPARATOR = "_id-id_";
	private final String NODE_SEPARATOR = "_id-node_";
	
	private final String REPLACEMENT_COLON = "U003A";
	private final String REPLACEMENT_SLASH = "U2215";
	
	/**
	 * BPMN model (org.camunda.bpm.model.bpmn.BpmnModelInstance)
	 */
	private transient BpmnModelInstance bpmnModel;
	
	/**
	 * initialize model
	 * @param connectionHandler input connection handler
	 * @throws IOException
	 */
	public void initModel(ConnectionHandler connectionHandler) throws IOException {
		
		//check connection handler
		if(connectionHandler == null || !connectionHandler.isValid())
			throw new IOException("connection handler is null or not valid");
		
		//init empty model
		bpmnModel = createEmptyModel("http://tu-dresden.de/uw/geo/gis/fusion");
		
		//create process
		Process bpmnProcess = createBPMNProcess(ID_SUFFIX + "chain_" + UUID.randomUUID());
		bpmnModel.getDefinitions().addChildElement(bpmnProcess);
		
		Set<String> startIds = new HashSet<String>();
		Set<String> endIds = new HashSet<String>();
		
		//add tasks
		for(IOProcess ioProcess : connectionHandler.getProcessSequence()){
			ServiceTask task = createBPMNTask(ioProcess, connectionHandler.getConnections(ioProcess));
			bpmnProcess.addChildElement(task);
			//set start/end event ids
			if(ioProcess.isStart())
				startIds.add(task.getId());
			if(ioProcess.isEnd())
				endIds.add(task.getId());
		}
		
		//add start event with sequence flows
		bpmnProcess.addChildElement(createStartEvent(bpmnProcess, startIds));
		
		//add end event with sequence flows
		bpmnProcess.addChildElement(createEndEvent(bpmnProcess, endIds));
		
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
		
		//add all sequence flows
		for(SequenceFlow sequenceFlow : sequenceFlows.values()) {
			bpmnProcess.addChildElement(sequenceFlow);
			//add incomming
			getModelNodeById(sequenceFlow.getAttributeValue("targetRef")).addChildElement(createIncoming(sequenceFlow.getId()));			
			//add outgoing
			getModelNodeById(sequenceFlow.getAttributeValue("sourceRef")).addChildElement(createOutgoing(sequenceFlow.getId()));
		}

	}

	/**
	 * create empty model with definitions
	 * @return model instance
	 */
	private BpmnModelInstance createEmptyModel(String namespace) {
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace(namespace);
		definitions.setId(ID_SUFFIX + "defs_" + UUID.randomUUID());
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
	 * create a start event for BPMN and sequence flows
	 * @param startIds outgoing sequence flow ids
	 * @return start event
	 */
	private StartEvent createStartEvent(Process bpmnProcess, Set<String> startIds) {
		//create start event
		StartEvent startEvent = bpmnModel.newInstance(StartEvent.class);
		startEvent.setId("_startEvent");
		for(String id : startIds){
			//add flow element
			bpmnProcess.addChildElement(createSequenceFlow(getSequenceFlowId(startEvent.getId(), id), startEvent.getId(), id));
			//add incoming and outgoing element
			startEvent.addChildElement(createOutgoing(getSequenceFlowId(startEvent.getId(), id)));
			bpmnModel.getModelElementById(id).addChildElement(createIncoming(getSequenceFlowId(startEvent.getId(), id)));			
		}
		return startEvent;
	}

	/**
	 * create a end event for BPMN and sequence flows
	 * @param endIds incoming sequence flow ids
	 * @return end event
	 */
	private EndEvent createEndEvent(Process bpmnProcess, Set<String> endIds) {
		//create end event
		EndEvent endEvent = bpmnModel.newInstance(EndEvent.class);
		endEvent.setId("_endEvent");
		for(String id : endIds){
			//add flow element
			bpmnProcess.addChildElement(createSequenceFlow(getSequenceFlowId(id, endEvent.getId()), id, endEvent.getId()));
			//add incoming and outgoing element
			bpmnModel.getModelElementById(id).addChildElement(createOutgoing(getSequenceFlowId(id, endEvent.getId())));		
			endEvent.addChildElement(createIncoming(getSequenceFlowId(id, endEvent.getId())));
		}
		return endEvent;
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
		bpmnTask.setId(getBPMNTaskId(ioProcess));
		bpmnTask.setName(getBPMNTaskName(ioProcess));
		bpmnTask.setExtensionElements(createProcessExtensionElements(ioProcess));
		
		//create io specification
		bpmnTask.addChildElement(createIOSpecification(ioProcess.getNodes()));
		
		//create io associations
		for(IOConnection ioConnection : ioConnections){
			if(ioConnection.getStartProcess().equals(ioProcess)){
				bpmnTask.addChildElement(createOutputAssociation(ioConnection));
			}
			if(ioConnection.getEndProcess().equals(ioProcess)){
				bpmnTask.addChildElement(createInputAssociation(ioConnection));
			}
		}

		return bpmnTask;
	}

	/**
	 * get Camunda extension elements for service and local identifiers of a service
	 * @param ioProcess input process
	 * @return extension elements for service and local identifier
	 */
	private ExtensionElements createProcessExtensionElements(IOProcess ioProcess) {
		
		//get camunda property for service and local id
		CamundaProperty propertyST = createCamundaProperty("serviceType_" + ioProcess.getUUID(), "serviceType", ioProcess.getServiceType());

		//get camunda properties
		CamundaProperties properties = bpmnModel.newInstance(CamundaProperties.class);
		properties.addChildElement(propertyST);
		for(Map.Entry<String,String> property : ioProcess.getProperties().entrySet()){
			CamundaProperty cProperty = createCamundaProperty(property.getKey() + "_" + ioProcess.getUUID(), property.getKey(), property.getValue());
			properties.addChildElement(cProperty);
		}
		
		//get extension elements
		ExtensionElements extensionElements = bpmnModel.newInstance(ExtensionElements.class);
		extensionElements.addChildElement(properties);		
		return extensionElements;
	}

	/**
	  * get BPMN properties (specified by Camunda)
	  * @param id property id
	  * @param name property name
	  * @param value property value
	  * @return Camunda property for BPMN
	  */
	private CamundaProperty createCamundaProperty(String id, String name, String value){
		CamundaProperty property = bpmnModel.newInstance(CamundaProperty.class);
		property.setCamundaId(id);
		property.setCamundaName(name);
		property.setCamundaValue(value);
		return property;
	}
	
	private String getBPMNTaskName(IOProcess ioProcess){
		return escapeString(NAME_SUFFIX + ioProcess.getName());
	}
	
	private String getBPMNTaskId(IOProcess ioProcess){
		return escapeString(ID_SUFFIX + ioProcess.getUUID());
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
		return escapeString(ID_SUFFIX + getBPMNTaskId(ioNode.getProcess()) + NODE_SEPARATOR + getBPMNNodeName(ioNode));
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
		source.setTextContent(getBPMNNodeId(connection.getStart()));
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
		return escapeString(getBPMNNodeId(connection.getStart()) + ID_SEPARATOR + getBPMNNodeId(connection.getEnd()));
	}
	
	private String getAssociationIdHash(IOConnection connection){
		return escapeString(getHashId(getAssociationId(connection)));
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
		return createSequenceFlow(getSequenceFlowId(connection), 
				getBPMNTaskId(connection.getStart().getProcess()), 
				getBPMNTaskId(connection.getEnd().getProcess()));
	}
	
	/**
	 * create sequence flow from io connection
	 * @param id flow id
	 * @param sourceId source element id
	 * @param targetId target element id
	 * @return sequence flow element
	 */
	private SequenceFlow createSequenceFlow(String id, String sourceId, String targetId) {
		SequenceFlow sequenceFlow = bpmnModel.newInstance(SequenceFlow.class);
		sequenceFlow.setId(id);
		sequenceFlow.setAttributeValue("sourceRef", sourceId);
		sequenceFlow.setAttributeValue("targetRef", targetId);
		return sequenceFlow;
	}
	
	private String getSequenceFlowId(IOConnection connection){
		return getSequenceFlowId(getBPMNTaskId(connection.getStart().getProcess()), getBPMNTaskId(connection.getEnd().getProcess()));
	}
	
	private String getSequenceFlowId(String sourceId, String targetId){
		return escapeString(ID_SUFFIX + sourceId + ID_SEPARATOR + targetId);
	}
	
	/**
	 * escape and normalize String to match NCName declaration
	 * @param string input string escaped string
	 * @return escaped and normalized string
	 */
	private String escapeString(String string){
		return string.replace(":", REPLACEMENT_COLON).replace("/", REPLACEMENT_SLASH).replaceAll("_+", "_");
	}
	
	/**
	 * generate hashcode from String
	 * @param input input string
	 * @return hashcode (0 - MAX_INT)
	 */
	private String getHashId(Object input){
		return escapeString(ID_SUFFIX + String.valueOf(Math.abs(input.hashCode())));
	}
	
	/**
	 * get model node by id
	 * @param id model node id
	 * @return model node
	 * @throws IOException if no model node can be found for specified id
	 */
	private ModelElementInstance getModelNodeById(String id) throws IOException {		
		ModelElementInstance node = bpmnModel.getModelElementById(id);
		if(node == null)
			throw new IOException("Element with id " + id + " not found");
		return node;
	}
	
	/**
	 * create incoming BPMN flow
	 * @param id flow id
	 * @return incoming for flow id
	 */
	private Incoming createIncoming(String id) {
		Incoming incoming = bpmnModel.newInstance(Incoming.class);
		incoming.setTextContent(id);
		return incoming;
	}

	/**
	 * create outgoing BPMN flow
	 * @param id flow id
	 * @return outgoing for flow id
	 */
	private Outgoing createOutgoing(String id) {
		Outgoing outgoing = bpmnModel.newInstance(Outgoing.class);
		outgoing.setTextContent(id);
		return outgoing;
	}
	
	/**
	 * get BPMN XML representation
	 * @return XML string
	 * @throws IOException 
	 * @throws ModelValidationException
	 */
	public String asXML() throws IOException {
		try {
//			System.out.println(IoUtil.convertXmlDocumentToString(bpmnModel.getDocument()));
			return Bpmn.convertToString(bpmnModel);
		} catch (ModelValidationException mve) {
			mve.printStackTrace();
			throw new IOException("could not create model: " + mve.getLocalizedMessage());
		}
		
	}
	
}