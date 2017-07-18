package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.camunda.bpm.model.bpmn.impl.instance.SourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Camunda BPNM workflow description
 */
public class CamundaBPMNWorkflow extends Workflow implements IWorkflow {

    private CamundaBPMNModel model;
    private Map<String,Class<? extends AbstractOperation>> operations = new HashMap<>();

    /**
     * constructor
     *
     * @param model Camunda BPMN model
     */
    public CamundaBPMNWorkflow(@NotNull CamundaBPMNModel model) {
        super();
        if(!model.isValid())
            throw new IllegalArgumentException("Model is not valid");
        this.model = model;
        this.initializeOperations();
        this.initializeWorkflow();
    }

    private void initializeWorkflow() {

        //initialize workflow nodes from BPMN tasks
        Collection<Task> tasks = this.model.getBPNMTasks();
        for(Task task : tasks) {
            if (task instanceof ServiceTask) {
                this.initilizeServiceTask((ServiceTask) task);
            } else if (task instanceof ScriptTask) {
                this.initilizeScriptTask((ScriptTask) task);
            } else
                throw new UnsupportedOperationException("task type " + task.getClass().getSimpleName() + " is not supported");
        }

        //initialize io connections
        initIOAssociations();

    }

    private void initIOAssociations() {

        Collection<DataObjectReference> ioReferences = this.model.getBPNMElements(DataObjectReference.class);

        for(DataObjectReference ioReference : ioReferences){

            Collection<IInputConnector> inputConnectors = identifyInputConnectors(ioReference.getId());
            Collection<IOutputConnector> outputConnectors = identifyOutputConnectors(ioReference.getId());

            //connect
            if(!inputConnectors.isEmpty() && !outputConnectors.isEmpty()){
                for(IOutputConnector outputConnector : outputConnectors){
                    for(IInputConnector inputConnector : inputConnectors){
                        outputConnector.connect(inputConnector);
                    }
                }
            }
            else {
                //check for workflow inputs (output connectors are empty)
                if (outputConnectors.isEmpty()) {
                    for(IInputConnector inputConnector : inputConnectors){
                        this.addInputConnector(inputConnector);
                    }
                }

                //check for workflow outputs (input connectors are empty)
                if (inputConnectors.isEmpty()) {
                    for(IOutputConnector outputConnector : outputConnectors){
                        this.addOutputConnector(outputConnector);
                    }
                }
            }
        }
    }

    private @NotNull Collection<IInputConnector> identifyInputConnectors(@NotNull String ioReference){

        //get source refs
        Collection<SourceRef> sourceRefs = this.model.getBPMNElementByTextContent(ioReference, SourceRef.class);

        //get associated input connectors
        Collection<IInputConnector> inputConnectors = new HashSet<>();
        for(SourceRef sourceRef : sourceRefs){
            //continue, if parent is not an input association
            if(!(sourceRef.getParentElement() instanceof DataInputAssociation))
                continue;
            //get input association
            DataInputAssociation inputAssociation = (DataInputAssociation) sourceRef.getParentElement();
            //get operation
            IWorkflowNode operation = this.getNode(((Task) inputAssociation.getParentElement()).getId());
            if(operation == null)
                //should not happen
                throw new RuntimeException("Operation for source ref '" + sourceRef + "' is not a valid operation");
            //get target ref
            TargetRef targetRef = (TargetRef) inputAssociation.getUniqueChildElementByType(TargetRef.class);
            //get connector name
            DataInput dataInput = (DataInput) this.model.getBPNMElementById(targetRef.getTextContent());
            String connectorTitle = dataInput.getName();
            //get connector
            inputConnectors.add(operation.getInputConnector(connectorTitle));
        }
        return inputConnectors;

    }

    private @NotNull Collection<IOutputConnector> identifyOutputConnectors(@NotNull String ioReference){

        //get target refs
        Collection<TargetRef> targetRefs = this.model.getBPMNElementByTextContent(ioReference, TargetRef.class);

        //get associated input connectors
        Collection<IOutputConnector> outputConnectors = new HashSet<>();
        for(TargetRef targetRef : targetRefs){
            //continue, if parent is not an output association
            if(!(targetRef.getParentElement() instanceof DataOutputAssociation))
                continue;
            //get output association
            DataOutputAssociation outputAssociation = (DataOutputAssociation) targetRef.getParentElement();
            //get operation
            IWorkflowNode operation = this.getNode(((Task) outputAssociation.getParentElement()).getId());
            if(operation == null)
                //should not happen
                throw new RuntimeException("Operation for target ref '" + targetRef + "' is not a valid operation");
            //get source ref
            SourceRef sourceRef = (SourceRef) outputAssociation.getUniqueChildElementByType(SourceRef.class);
            //get connector name
            DataOutput dataInput = (DataOutput) this.model.getBPNMElementById(sourceRef.getTextContent());
            String connectorTitle = dataInput.getName();
            //get connector
            outputConnectors.add(operation.getOutputConnector(connectorTitle));
        }
        return outputConnectors;

    }

    private void initilizeServiceTask(ServiceTask task) {

    }

    private void initilizeScriptTask(ScriptTask task) {
        IWorkflowNode node = this.getNodeFromClassName(task.getName(), new ResourceIdentifier(task.getId()));
        this.addNode(node);
    }

    private @NotNull IWorkflowNode getNodeFromClassName(@NotNull String className, @NotNull IIdentifier identifier){
        if(!this.operations.containsKey(className))
            throw new IllegalArgumentException("Operation " + className + " is not available");
        try {
            return this.operations.get(className).getConstructor(IIdentifier.class).newInstance(identifier);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not instantiate operation " + className, e);
        }
    }

    /**
     * initialize available operations
     */
    private void initializeOperations() {
        Reflections reflections = new Reflections("de.tudresden.geoinfo.fusion.operation");
        Set<Class<? extends AbstractOperation>> operations = reflections.getSubTypesOf(AbstractOperation.class);
        for(Class<? extends AbstractOperation> operation : operations){
            if(!operation.isInterface() || !Modifier.isAbstract(operation.getModifiers())){
                this.operations.put(operation.getName(), operation);
            }
        }
    }

}
