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

    private final static String MODEL_NAMESPACE = "http://tu-dresden.de/uw/geo/gis/fusion";
    private Set<String> startTaskIds = new HashSet<>();
    private Set<String> endTaskIds = new HashSet<>();

    /**
     * constructor
     */
    public CamundaBPMNModel(@Nullable IIdentifier identifier, @NotNull BpmnModelInstance bpmnModelInstance) {
        super(identifier, bpmnModelInstance);
    }

    /**
     * constructor
     */
    public CamundaBPMNModel(@Nullable IIdentifier identifier, @NotNull IWorkflow workflow) {
        this(identifier, createEmptyModel(identifier != null ? identifier : new Identifier()));
        this.initModel(workflow);
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
     * initialize the BPMN model from an existing workflow
     *
     * @param workflow input workflow
     */
    public void initModel(IWorkflow workflow) {

        //add process
        Process process = createBPMNProcess(workflow.getIdentifier().toString());
        this.resolve().getDefinitions().addChildElement(process);

        //add all data objects from workflow connections
        Map<String, SequenceFlow> sequenceFlows = new HashMap<>();
        for (IWorkflowConnection connection : workflow.getWorkflowConnections()) {
            //add data object
            DataObject dataObject = createDataObject(this.getUUID("dataObject_"), connection.getTitle(), null);
            process.addChildElement(dataObject);
            process.addChildElement(createDataObjectRef(connection.getIdentifier().toString(), dataObject.getId()));
            //create sequence flow, if tasks are not yet connected
            String sequenceFlowId = connection.getOutputConnector().getEntity().getIdentifier().toString() + "_" + connection.getInputConnector().getEntity().getIdentifier().toString();
            if (!sequenceFlows.containsKey(sequenceFlowId))
                sequenceFlows.put(sequenceFlowId, createSequenceFlow(connection));
        }

        //add all data objects from workflow IO
        for(IInputConnector inputConnector : workflow.getInputConnectors()){
            //add data object
            DataObject dataObject = createDataObject(this.getUUID("workflowInput_"), inputConnector.getTitle(), null);
            process.addChildElement(dataObject);
            process.addChildElement(createDataObjectRef("data" + inputConnector.getIdentifier().toString(), dataObject.getId()));
        }
        for(IOutputConnector outputConnector : workflow.getOutputConnectors()){
            //add data object
            DataObject dataObject = createDataObject(this.getUUID("workflowOutput_"), outputConnector.getTitle(), null);
            process.addChildElement(dataObject);
            process.addChildElement(createDataObjectRef("data" + outputConnector.getIdentifier().toString(), dataObject.getId()));
        }

        //add all BPMN tasks
        for (IWorkflowNode node : workflow.getWorkflowNodes()) {
            process.addChildElement(createBPMNTask(node, workflow));
        }

        //create start and end event
        process.addChildElement(createStartEvent(process));
        process.addChildElement(createEndEvent(process));

        //add all sequence flows
        for (SequenceFlow sequenceFlow : sequenceFlows.values()) {
            process.addChildElement(sequenceFlow);
            //add incoming to service task
            getModelNodeById(sequenceFlow.getAttributeValue("targetRef")).addChildElement(createIncoming(sequenceFlow.getId()));
            //add outgoing to service task
            getModelNodeById(sequenceFlow.getAttributeValue("sourceRef")).addChildElement(createOutgoing(sequenceFlow.getId()));
        }
    }

    private DataObject createDataObject(@NotNull String identifier, String title, Object value) {
        DataObject dataObject = this.newInstance(DataObject.class);
        dataObject.setId(identifier);
        dataObject.setName(title);
        return dataObject;
    }

    /**
     * create data object reference for connection
     *
     * @param identifier reference identifier
     */
    private DataObjectReference createDataObjectRef(String identifier, String objId) {
        DataObjectReference dataObjectRef = this.newInstance(DataObjectReference.class);
        dataObjectRef.setId(identifier);
        dataObjectRef.setAttributeValue("dataObjectRef", objId);
        return dataObjectRef;
    }

    /**
     * create sequence flow from identifiers
     *
     * @param sourceId source element id
     * @param targetId target element id
     * @return sequence flow element
     */
    private SequenceFlow createSequenceFlow(String sourceId, String targetId) {
        SequenceFlow sequenceFlow = this.newInstance(SequenceFlow.class);
        sequenceFlow.setId(this.getUUID(sequenceFlow));
        sequenceFlow.setAttributeValue("sourceRef", sourceId);
        sequenceFlow.setAttributeValue("targetRef", targetId);
        return sequenceFlow;
    }

    /**
     * create sequence flow from io connection
     *
     * @param connection io connection
     * @return sequence flow for connection
     */
    private SequenceFlow createSequenceFlow(IWorkflowConnection connection) {
        return createSequenceFlow(
                connection.getOutputConnector().getEntity().getIdentifier().toString(),
                connection.getInputConnector().getEntity().getIdentifier().toString());
    }

    /**
     * create incoming BPMN flow
     *
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
     *
     * @param id flow id
     * @return outgoing for flow id
     */
    private Outgoing createOutgoing(String id) {
        Outgoing outgoing = this.newInstance(Outgoing.class);
        outgoing.setTextContent(id);
        return outgoing;
    }

    /**
     * create BPMN process
     *
     * @return BPMN process definition
     */
    private Process createBPMNProcess(String identifier) {
        Process bpmnProcess = this.newInstance(Process.class);
        bpmnProcess.setId(identifier);
        return bpmnProcess;
    }

    /**
     * add BPMN task
     *
     * @param node workflow node
     * @return BPMN task
     */
    private Task createBPMNTask(IWorkflowNode node, IWorkflow workflow) {
        Task bpmnTask;
//        if(node instanceof InputNode) {
//            bpmnTask = this.createBPMNTask(node, this.newInstance(UserTask.class));
//            bpmnTask.setExtensionElements(createInputExtensionElements((InputNode) node));
//        }
        if (node instanceof OWSServiceOperation) {
            bpmnTask = this.createBPMNTask(node, this.newInstance(ServiceTask.class), workflow);
            bpmnTask.setExtensionElements(createOWSExtensionElements((OWSServiceOperation) node));
        } else
            bpmnTask = this.createBPMNTask(node, this.newInstance(ScriptTask.class), workflow);
        //add task to start or end node, if required
        if (node.getAncestors().isEmpty())
            this.startTaskIds.add(bpmnTask.getId());
        if (node.getSuccessors().isEmpty())
            this.endTaskIds.add(bpmnTask.getId());
        return bpmnTask;
    }

    /**
     * create a BPMN servicetask
     *
     * @param node input workflow node
     * @return service task
     */
    private Task createBPMNTask(IWorkflowNode node, Task bpmnTask, IWorkflow workflow) {
        //create BPMN process
        bpmnTask.setId(node.getIdentifier().toString());
        bpmnTask.setName(node.getTitle());
        //create io specification
        bpmnTask.addChildElement(createIOSpecification(node));
        //create io associations
        for (IInputConnector inConnector : node.getInputConnectors()) {
            if (!inConnector.getConnections().isEmpty()) {
                for (IWorkflowConnection connection : inConnector.getConnections()) {
                    bpmnTask.addChildElement(createInputAssociation(connection));
                }
            } else if (workflow.getInputConnectors().contains(inConnector)) {
                bpmnTask.addChildElement(createInputAssociation(inConnector));
            }
        }
        for (IOutputConnector outConnector : node.getOutputConnectors()) {
            if (!outConnector.getConnections().isEmpty()) {
                for (IWorkflowConnection connection : outConnector.getConnections()) {
                    bpmnTask.addChildElement(createOutputAssociation(connection));
                }
            } else if (workflow.getOutputConnectors().contains(outConnector)) {
                bpmnTask.addChildElement(createOutputAssociation(outConnector));
            }
        }
        return bpmnTask;
    }

    /**
     * create OWS extension elements
     *
     * @param node input node
     * @return extension element set
     */
    private ExtensionElements createOWSExtensionElements(OWSServiceOperation node) {
        CamundaProperties properties = this.newInstance(CamundaProperties.class);
        //create properties
        properties.addChildElement(createCamundaProperty("type", "OWS"));
        properties.addChildElement(createCamundaProperty("service", node.getService()));
        properties.addChildElement(createCamundaProperty("base", node.getBase().toString()));
        properties.addChildElement(createCamundaProperty("version", node.getDefaultVersion()));
        properties.addChildElement(createCamundaProperty("offering", node.getSelectedOffering()));
        //set extension elements
        ExtensionElements extensionElements = this.newInstance(ExtensionElements.class);
        extensionElements.addChildElement(properties);
        return extensionElements;
    }

    /**
     * create input extension elements
     * @param node literal input node
     * @return extension element set
     */
//    private ExtensionElements createInputExtensionElements(InputNode node) {
//        CamundaProperties properties = this.newInstance(CamundaProperties.class);
//        //create properties
//        for(IOutputConnector connector : node.getOutputConnectors()){
//            if(connector.getData() != null)
//                properties.addChildElement(createCamundaProperty(connector.getTitle(), connector.getData().resolve().toString()));
//        }
//        //set extension elements
//        ExtensionElements extensionElements = this.newInstance(ExtensionElements.class);
//        extensionElements.addChildElement(properties);
//        return extensionElements;
//    }

    /**
     * get BPMN properties (specified by Camunda)
     *
     * @param name  property name
     * @param value property value
     * @return Camunda property for BPMN
     */
    private CamundaProperty createCamundaProperty(String name, String value) {
        CamundaProperty property = this.newInstance(CamundaProperty.class);
        property.setCamundaId(this.getUUID(property));
        property.setCamundaName(name);
        property.setCamundaValue(value);
        return property;
    }

    /**
     * create a task IO specification
     *
     * @param node input node
     * @return IO specification element
     */
    private IoSpecification createIOSpecification(IWorkflowNode node) {
        //create IoSpecification
        IoSpecification ioSpec = this.newInstance(IoSpecification.class);
        //create data inputs and outputs
        for (IInputConnector inConnector : node.getInputConnectors()) {
            ioSpec.addChildElement(createDataInput(inConnector));
        }
        for (IOutputConnector outConnector : node.getOutputConnectors()) {
            ioSpec.addChildElement(createDataOutput(outConnector));
        }
        //create input and output sets
        ioSpec.addChildElement(createDataInputSet(ioSpec.getDataInputs()));
        ioSpec.addChildElement(createDataOutputSet(ioSpec.getDataOutputs()));
        return ioSpec;
    }

    /**
     * create data input as part of IO specification
     *
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
     *
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
     *
     * @param dataInputs input data collection
     * @return Input set element
     */
    private InputSet createDataInputSet(Collection<DataInput> dataInputs) {
        InputSet inSet = this.newInstance(InputSet.class);
        //add references
        for (DataInput input : dataInputs) {
            DataInputRefs ref = this.newInstance(DataInputRefs.class);
            ref.setTextContent(input.getId());
            inSet.addChildElement(ref);
        }
        return inSet;
    }

    /**
     * create Output set as part of IO specification
     *
     * @param dataOutputs output data collection
     * @return Output set element
     */
    private OutputSet createDataOutputSet(Collection<DataOutput> dataOutputs) {
        OutputSet outSet = this.newInstance(OutputSet.class);
        //add references
        for (DataOutput output : dataOutputs) {
            DataOutputRefs ref = this.newInstance(DataOutputRefs.class);
            ref.setTextContent(output.getId());
            outSet.addChildElement(ref);
        }
        return outSet;
    }

    /**
     * create a BPMN task input association
     *
     * @param connection input connection
     * @return data association element
     */
    private DataInputAssociation createInputAssociation(IWorkflowConnection connection) {
        return (DataInputAssociation) this.createIOAssociation(connection.getIdentifier().toString(), connection.getInputConnector().getIdentifier().toString(), true);
    }

    private DataAssociation createIOAssociation(String sourceId, String targetId, boolean isInputAssociation) {
        //create BPMN data association
        DataAssociation association = isInputAssociation ? this.newInstance(DataInputAssociation.class) : this.newInstance(DataOutputAssociation.class);
        //set source reference
        SourceRef source = this.newInstance(SourceRef.class);
        source.setTextContent(sourceId);
        association.addChildElement(source);
        //set target reference
        TargetRef target = this.newInstance(TargetRef.class);
        target.setTextContent(targetId);
        association.addChildElement(target);
        return association;
    }

    private DataInputAssociation createInputAssociation(IInputConnector connector) {
        return (DataInputAssociation) this.createIOAssociation("data" + connector.getIdentifier().toString(), connector.getIdentifier().toString(), true);
    }

    private DataOutputAssociation createOutputAssociation(IOutputConnector connector) {
        return (DataOutputAssociation) this.createIOAssociation(connector.getIdentifier().toString(), "data" + connector.getIdentifier().toString(), false);
    }

    /**
     * create a BPMN task output association
     *
     * @param connection output connection
     * @return data association element
     */
    private DataOutputAssociation createOutputAssociation(IWorkflowConnection connection) {
        return (DataOutputAssociation) this.createIOAssociation(connection.getOutputConnector().getIdentifier().toString(), connection.getIdentifier().toString(), false);
    }

    /**
     * create start event for BPMN and sequence flows
     *
     * @return start event
     */
    private StartEvent createStartEvent(Process process) {
        //create start event
        StartEvent startEvent = this.newInstance(StartEvent.class);
        startEvent.setId("_startEvent");
        for (String id : this.startTaskIds) {
            //add flow element
            SequenceFlow flow = createSequenceFlow(startEvent.getId(), id);
            process.addChildElement(flow);
            //add incoming and outgoing element
            startEvent.addChildElement(createOutgoing(flow.getId()));
            this.resolve().getModelElementById(id).addChildElement(createIncoming(flow.getId()));
        }
        return startEvent;
    }

    /**
     * create end event for BPMN and sequence flows
     *
     * @return start event
     */
    private EndEvent createEndEvent(Process process) {
        //create start event
        EndEvent endEvent = this.newInstance(EndEvent.class);
        endEvent.setId("_endEvent");
        for (String id : this.endTaskIds) {
            //add flow element
            SequenceFlow flow = createSequenceFlow(id, endEvent.getId());
            process.addChildElement(flow);
            //add incoming and outgoing element
            endEvent.addChildElement(createIncoming(flow.getId()));
            this.resolve().getModelElementById(id).addChildElement(createOutgoing(flow.getId()));
        }
        return endEvent;
    }

    /**
     * get model node by id
     *
     * @param id model node id
     * @return model node with corresponding id
     */
    private ModelElementInstance getModelNodeById(String id) {
        ModelElementInstance node = this.resolve().getModelElementById(id);
        if (node == null)
            throw new IllegalArgumentException("Element with id " + id + " not found");
        return node;
    }

    /**
     * get UUID valid for XML NCName
     *
     * @return
     */
    private String getUUID(Object object) {
        return this.getUUID(object.getClass().getSimpleName() + "_");
    }

    /**
     * get UUID valid for XML NCName
     *
     * @return
     */
    private String getUUID(String prefix) {
        return prefix + UUID.randomUUID();
    }

    /**
     * get Camunda BPMN instance (short for this.resolve().newInstance())
     *
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
        } catch (ModelValidationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * check, if model is valid
     * @return true, if model is validated without exception
     */
    boolean isValid() {
        try {
            Bpmn.validateModel(this.resolve());
            return true;
        } catch (ModelValidationException mve) {
            return false;
        }
    }

    /**
     * get BPMN tasks of the workflow
     *
     * @return BPMN tasks
     */
    public Collection<Task> getBPNMTasks() {
        return getBPNMElements(Task.class);
    }

    /**
     * get BPMN elements from model
     * @param clazz element class
     * @param <T> type of element
     * @return collection of BPMN elements
     */
    public <T extends ModelElementInstance> Collection<T> getBPNMElements(Class<T> clazz){
        return this.resolve().getModelElementsByType(clazz);
    }

    /**
     * get BPNM element by id
     * @param identifier element identifier
     * @return BPMN element
     */
    public ModelElementInstance getBPNMElementById(String identifier){
        return this.resolve().getModelElementById(identifier);
    }

    /**
     * get collection of BPMN elements with a certain text input
     * @param text search input text
     * @param clazz element class
     * @param <T> type of element
     * @return collection of elements with requested input text content
     */
    public @NotNull <T extends ModelElementInstance> Collection<T> getBPMNElementByTextContent(String text, Class<T> clazz){
        Collection<T> elements = this.getBPNMElements(clazz);
        elements.removeIf(element -> !element.getTextContent().equals(text));
        return elements;
    }

}
