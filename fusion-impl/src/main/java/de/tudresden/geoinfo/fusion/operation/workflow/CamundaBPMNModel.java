package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.ows.OWSServiceOperation;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.*;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Camunda BPNM workflow description
 */
public class CamundaBPMNModel extends Data<BpmnModelInstance> {

    private Process bpmnProcess;
    private Set<String> startTaskIds = new HashSet<>();
    private Set<String> endTaskIds = new HashSet<>();

    private final static String MODEL_NAMESPACE = "http://tu-dresden.de/uw/geo/gis/fusion";

    /**
     * constructor
     */
    public CamundaBPMNModel(@Nullable IIdentifier identifier, @NotNull String title, @Nullable String description) {
        super(identifier, createEmptyModel(identifier != null ? identifier : new Identifier()), title, description);
    }

    /**
     * initialize the BPMN model from an existing workflow
     * @param workflow input workflow
     */
    public void initModel(IWorkflow workflow) {
        if(this.bpmnProcess != null)
            throw new UnsupportedOperationException("model has already been initialized");
        this.initBPMNProcess();
        //add all tasks as service tasks
        for(IWorkflowNode node : workflow.getWorkflowNodes()) {
            this.addBPMNTask(node);
        }
        //create start and end event
        this.bpmnProcess.addChildElement(createStartEvent());
        this.bpmnProcess.addChildElement(createEndEvent());
        //add data object for each connection
        Map<String,SequenceFlow> sequenceFlows = new HashMap<>();
        for(IWorkflowConnection connection : workflow.getWorkflowConnections()){
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
            //add incoming to service task
            getModelNodeById(sequenceFlow.getAttributeValue("targetRef")).addChildElement(createIncoming(sequenceFlow.getId()));
            //add outgoing to service task
            getModelNodeById(sequenceFlow.getAttributeValue("sourceRef")).addChildElement(createOutgoing(sequenceFlow.getId()));
        }
    }

    /**
     * create data object for connection
     * @param connection connection
     * @return data object for connection
     */
    private DataObject createDataObject(IWorkflowConnection connection) {
        DataObject dataObject = this.newInstance(DataObject.class);
        dataObject.setId(connection.getIdentifier().toString());
        return dataObject;
    }

    /**
     * create data object reference for connection
     * @param connection connection
     * @return data object reference for connection
     */
    private DataObjectReference createDataObjectRef(IWorkflowConnection connection) {
        DataObjectReference dataObjectRef = this.newInstance(DataObjectReference.class);
        dataObjectRef.setId("_" + connection.hashCode());
        dataObjectRef.setAttributeValue("dataObjectRef", connection.getIdentifier().toString());
        return dataObjectRef;
    }

    /**
     * create sequence flow from identifiers
     * @param id flow id
     * @param sourceId source element id
     * @param targetId target element id
     * @return sequence flow element
     */
    private SequenceFlow createSequenceFlow(String id, String sourceId, String targetId) {
        SequenceFlow sequenceFlow = this.newInstance(SequenceFlow.class);
        sequenceFlow.setId(id);
        sequenceFlow.setAttributeValue("sourceRef", sourceId);
        sequenceFlow.setAttributeValue("targetRef", targetId);
        return sequenceFlow;
    }

    /**
     * create sequence flow from io connection
     * @param connection io connection
     * @return sequence flow for connection
     */
    private SequenceFlow createSequenceFlow(IWorkflowConnection connection) {
        return createSequenceFlow(this.getUUID(),
                connection.getOutput().getEntity().getIdentifier().toString(),
                connection.getInput().getEntity().getIdentifier().toString());
    }

    /**
     * create incoming BPMN flow
     * @param id flow id
     * @return incoming for flow id
     */
    private Incoming createIncoming(String id) {
        Incoming incoming = this.newInstance(Incoming.class);
        incoming.setTextContent(id);
        return incoming;
    }

    /**
     * create outgoing BPMN flow
     * @param id flow id
     * @return outgoing for flow id
     */
    private Outgoing createOutgoing(String id) {
        Outgoing outgoing = this.newInstance(Outgoing.class);
        outgoing.setTextContent(id);
        return outgoing;
    }

    /**
     * create empty model with definitions
     *
     * @param identifier identifier for bpmn definitions
     * @return model instance
     */
    private static BpmnModelInstance createEmptyModel(@NotNull IIdentifier identifier) {
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace(MODEL_NAMESPACE);
        definitions.setId(identifier.toString());
        modelInstance.setDefinitions(definitions);
        return modelInstance;
    }

    /**
     * create BPMN process
     *
     * @return BPMN process definition
     */
    private void initBPMNProcess() {
        //create BPMN process
        this.bpmnProcess = this.newInstance(Process.class);
        bpmnProcess.setId("bpmn" + this.getUUID());
        this.resolve().getDefinitions().addChildElement(bpmnProcess);
    }

    /**
     * add BPMN task
     *
     * @param node workflow node
     * @return BPMN task
     */
    private void addBPMNTask(IWorkflowNode node) {
        Task bpmnTask;
        if(node instanceof InputData)
            bpmnTask = this.createBPMNTask(node, this.newInstance(UserTask.class));
        else if(node instanceof OWSServiceOperation)
            bpmnTask = this.createBPMNTask(node, this.newInstance(ServiceTask.class));
        else
            bpmnTask = this.createBPMNTask(node, this.newInstance(ScriptTask.class));
        //add task to start or end node, if required
        if(node.getAncestors().isEmpty())
            startTaskIds.add(bpmnTask.getId());
        if(node.getSuccessors().isEmpty())
            endTaskIds.add(bpmnTask.getId());
        //add task to process
        this.bpmnProcess.addChildElement(bpmnTask);
    }

    /**
     * create a BPMN servicetask
     * @param node input workflow node
     * @return service task
     */
    private Task createBPMNTask(IWorkflowNode node, Task bpmnTask) {
        //create BPMN process
        bpmnTask.setId(node.getIdentifier().toString());
        bpmnTask.setName(node.getTitle());
        bpmnTask.setExtensionElements(createProcessExtensionElements(node));
        //create io specification
        bpmnTask.addChildElement(createIOSpecification(node));
        //create io associations
        for(IInputConnector inConnector : node.getInputConnectors()){
            if(!inConnector.getConnections().isEmpty()){
                for(IWorkflowConnection connection : inConnector.getConnections()){
                    bpmnTask.addChildElement(createInputAssociation(connection));
                }
            }
        }
        for(IOutputConnector outConnector : node.getOutputConnectors()){
            if(!outConnector.getConnections().isEmpty()){
                for(IWorkflowConnection connection : outConnector.getConnections()){
                    bpmnTask.addChildElement(createOutputAssociation(connection));
                }
            }
        }
        return bpmnTask;
    }

    /**
     * create OWS extension elements
     * @param node input node
     * @return extension element set
     */
    private ExtensionElements createProcessExtensionElements(IWorkflowNode node) {
        CamundaProperties properties = this.newInstance(CamundaProperties.class);
        if(node instanceof OWSServiceOperation){
            //init OWS properties
            properties.addChildElement(createCamundaProperty("type", "OWS"));
            properties.addChildElement(createCamundaProperty("service", ((OWSServiceOperation) node).getService()));
            properties.addChildElement(createCamundaProperty("base", ((OWSServiceOperation) node).getBase().toString()));
            properties.addChildElement(createCamundaProperty("version", ((OWSServiceOperation) node).getDefaultVersion()));
            properties.addChildElement(createCamundaProperty("offering", ((OWSServiceOperation) node).getSelectedOffering()));
        }
        //set extension elements
        ExtensionElements extensionElements = this.newInstance(ExtensionElements.class);
        extensionElements.addChildElement(properties);
        return extensionElements;
    }

    /**
     * get BPMN properties (specified by Camunda)
     * @param name property name
     * @param value property value
     * @return Camunda property for BPMN
     */
    private CamundaProperty createCamundaProperty(String name, String value){
        CamundaProperty property = this.newInstance(CamundaProperty.class);
        property.setCamundaId(this.getUUID());
        property.setCamundaName(name);
        property.setCamundaValue(value);
        return property;
    }

    /**
     * create a task IO specification
     * @param node input node
     * @return IO specification element
     */
    private IoSpecification createIOSpecification(IWorkflowNode node) {
        //create IoSpecification
        IoSpecification ioSpec = this.newInstance(IoSpecification.class);
        //create data inputs and outputs
        for(IInputConnector inConnector : node.getInputConnectors()){
            ioSpec.addChildElement(createDataInput(inConnector));
        }
        for(IOutputConnector outConnector : node.getOutputConnectors()){
            ioSpec.addChildElement(createDataOutput(outConnector));
        }
        //create input and output sets
        ioSpec.addChildElement(createDataInputSet(ioSpec.getDataInputs()));
        ioSpec.addChildElement(createDataOutputSet(ioSpec.getDataOutputs()));
        return ioSpec;
    }

    /**
     * create data input as part of IO specification
     * @param connector input connection
     * @return IO data element
     */
    private DataInput createDataInput(IWorkflowConnector connector) {
        DataInput in = this.newInstance(DataInput.class);
        in.setId(connector.getIdentifier().toString());
        in.setName(connector.getTitle());
        return in;
    }

    /**
     * create data output as part of IO specification
     * @param connector input connection
     * @return IO data element
     */
    private DataOutput createDataOutput(IWorkflowConnector connector) {
        DataOutput out = this.newInstance(DataOutput.class);
        out.setId(connector.getIdentifier().toString());
        out.setName(connector.getTitle());
        return out;
    }

    /**
     * create Input set as part of IO specification
     * @param dataInputs input data collection
     * @return Input set element
     */
    private InputSet createDataInputSet(Collection<DataInput> dataInputs) {
        InputSet inSet = this.newInstance(InputSet.class);
        //add references
        for(DataInput input : dataInputs){
            DataInputRefs ref = this.newInstance(DataInputRefs.class);
            ref.setTextContent(input.getId());
            inSet.addChildElement(ref);
        }
        return inSet;
    }

    /**
     * create Output set as part of IO specification
     * @param dataOutputs output data collection
     * @return Output set element
     */
    private OutputSet createDataOutputSet(Collection<DataOutput> dataOutputs) {
        OutputSet outSet = this.newInstance(OutputSet.class);
        //add references
        for(DataOutput output : dataOutputs){
            DataOutputRefs ref = this.newInstance(DataOutputRefs.class);
            ref.setTextContent(output.getId());
            outSet.addChildElement(ref);
        }
        return outSet;
    }

    /**
     * create a BPMN task input association
     * @param connection input connection
     * @return data association element
     */
    private DataInputAssociation createInputAssociation(IWorkflowConnection connection) {
        //create BPMN data input association
        DataInputAssociation inAssociation = this.newInstance(DataInputAssociation.class);
        //set source reference
        SourceRef source = this.newInstance(SourceRef.class);
        source.setTextContent(connection.getIdentifier().toString());
        inAssociation.addChildElement(source);
        //set target reference
        TargetRef target = this.newInstance(TargetRef.class);
        target.setTextContent(connection.getOutput().getIdentifier().toString());
        inAssociation.addChildElement(target);
        return inAssociation;
    }

    /**
     * create a BPMN task output association
     * @param connection output connection
     * @return data association element
     */
    private DataOutputAssociation createOutputAssociation(IWorkflowConnection connection) {
        //create BPMN data input association
        DataOutputAssociation outAssociation = this.resolve().newInstance(DataOutputAssociation.class);
        //set source reference
        SourceRef source = this.resolve().newInstance(SourceRef.class);
        source.setTextContent(connection.getInput().getIdentifier().toString());
        outAssociation.addChildElement(source);
        //set target reference
        TargetRef target = this.newInstance(TargetRef.class);
        target.setTextContent(connection.getIdentifier().toString());
        outAssociation.addChildElement(target);
        return outAssociation;
    }

    /**
     * create start event for BPMN and sequence flows
     * @return start event
     */
    private StartEvent createStartEvent() {
        //create start event
        StartEvent startEvent = this.newInstance(StartEvent.class);
        startEvent.setId("_startEvent");
        for(String id : this.startTaskIds){
            //add flow element
            String identifier = this.getUUID();
            bpmnProcess.addChildElement(createSequenceFlow(identifier, startEvent.getId(), id));
            //add incoming and outgoing element
            startEvent.addChildElement(createOutgoing(identifier));
            this.resolve().getModelElementById(id).addChildElement(createIncoming(identifier));
        }
        return startEvent;
    }

    /**
     * create end event for BPMN and sequence flows
     * @return start event
     */
    private EndEvent createEndEvent() {
        //create start event
        EndEvent endEvent = this.newInstance(EndEvent.class);
        endEvent.setId("_endEvent");
        for(String id : this.endTaskIds){
            //add flow element
            String identifier = this.getUUID();
            bpmnProcess.addChildElement(createSequenceFlow(identifier, id, endEvent.getId()));
            //add incoming and outgoing element
            endEvent.addChildElement(createIncoming(identifier));
            this.resolve().getModelElementById(id).addChildElement(createOutgoing(identifier));
        }
        return endEvent;
    }

    /**
     * get model node by id
     * @param id model node id
     * @return model node with corresponding id
     */
    private ModelElementInstance getModelNodeById(String id) {
        ModelElementInstance node = this.resolve().getModelElementById(id);
        if(node == null)
            throw new IllegalArgumentException("Element with id " + id + " not found");
        return node;
    }

    /**
     * get UUID valid for XML NCName
     * @return
     */
    private String getUUID() {
        return "_" + UUID.randomUUID();
    }

    /**
     * get Camunda BPMN instance (short for this.resolve().newInstance())
     * @return BPMN model element instance
     */
    private <T extends ModelElementInstance> T newInstance(Class<T> type) {
        return this.resolve().newInstance(type);
    }

    /**
     * get XML string following BPMN 2.0 schema
     *
     * @return XML string
     */
    public String asXML() {
        try {
            return Bpmn.convertToString(this.resolve());
        } catch (ModelValidationException e){
            e.printStackTrace();
            return null;
        }
    }

}
