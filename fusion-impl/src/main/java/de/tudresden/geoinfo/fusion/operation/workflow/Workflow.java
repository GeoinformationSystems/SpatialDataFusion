package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * standard workflow operation
 */
public class Workflow extends AbstractOperation implements IWorkflow {

    private static final String PROCESS_TITLE = Workflow.class.getName();

    private Map<String,IWorkflowNode> nodes;

    /**
     * empty constructor, if used initializeConnectors() must be called after setting workflow nodes
     */
    public Workflow() {
        super(PROCESS_TITLE, null);
    }

    /**
     * Constructor
     *
     * @param nodes workflow nodes
     */
    public Workflow(@NotNull Collection<IWorkflowNode> nodes) {
        this();
        this.setNodes(nodes);
        super.initializeConnectors();
    }

    @Override
    protected void initializeInputConnectors() {
        Set<IInputConnector> inputConnectors = new HashSet<>();
        for (IWorkflowNode node : this.getWorkflowNodes()) {
            for (IInputConnector connector : node.getInputConnectors()) {
                if (connector.getConnections().isEmpty())
                    inputConnectors.add(connector);
            }
        }
        this.initializeInputConnectors(inputConnectors);
    }

    /**
     * add input connectors to workflow
     *
     * @param inputConnectors input connectors
     */
    public void initializeInputConnectors(Set<IInputConnector> inputConnectors) {
        super.clearInputConnectors();
        for (IInputConnector inputConnector : inputConnectors) {
            this.addInputConnector(inputConnector);
        }
    }

    @Override
    public void initializeOutputConnectors() {
        Set<IOutputConnector> outputConnectors = new HashSet<>();
        for (IWorkflowNode node : this.getWorkflowNodes()) {
            for (IOutputConnector connector : node.getOutputConnectors()) {
                if (connector.getConnections().isEmpty())
                    outputConnectors.add(connector);
            }
        }
        this.initializeOutputConnectors(outputConnectors);
    }

    /**
     * add output connectors to workflow
     *
     * @param outputConnectors output connectors
     */
    public void initializeOutputConnectors(Set<IOutputConnector> outputConnectors) {
        super.clearOutputConnectors();
        for (IOutputConnector outputConnector : outputConnectors) {
            this.addOutputConnector(outputConnector);
        }
    }

    @Override
    public void executeOperation() {
        executeNode(this.getWorkflowNodes().iterator().next());
    }

    @NotNull
    @Override
    public Collection<IWorkflowNode> getWorkflowNodes() {
        if(this.nodes == null)
            this.nodes = new HashMap<>();
        return this.nodes.values();
    }

    @Override
    public @NotNull Set<IWorkflowConnection> getWorkflowConnections() {
        Set<IWorkflowConnection> connections = new HashSet<>();
        for (IWorkflowNode node : this.getWorkflowNodes()) {
            for (IInputConnector inConnector : node.getInputConnectors()) {
                connections.addAll(inConnector.getConnections());
            }
            for (IOutputConnector outConnector : node.getOutputConnectors()) {
                connections.addAll(outConnector.getConnections());
            }
        }
        return connections;
    }

    /**
     * adds a workflow node
     *
     * @param node input workflow node
     */
    protected void addNode(@NotNull IWorkflowNode node) {
        this.nodes.put(node.getIdentifier().toString(), node);
    }

    /**
     * get a workflow node by id
     *
     * @param identifier node id
     */
    protected @Nullable IWorkflowNode getNode(@NotNull IIdentifier identifier) {
        return this.getNode(identifier.toString());
    }

    /**
     * get a workflow node by id
     *
     * @param sIdentifier node id
     */
    protected @Nullable IWorkflowNode getNode(@NotNull String sIdentifier) {
        return this.nodes.get(sIdentifier);
    }

    /**
     * set workflow nodes
     *
     * @param nodes input workflow nodes
     */
    protected void setNodes(@NotNull Collection<IWorkflowNode> nodes) {
        for (IWorkflowNode node : nodes)
            this.addNode(node);
    }

    /**
     * recursively execute nodes (execute ancestor nodes first, then perform operation, finally call successor node)
     *
     * @param node node to execute
     */
    private void executeNode(IWorkflowNode node) {
        //execute ancestors and self, if this node has not been executed yet
        if (!node.success()) {
            //execute ancestors, if have not been executed yet
            for (IWorkflowNode ancestor : node.getAncestors()) {
                if (!ancestor.success())
                    executeNode(ancestor);
            }
            //execute self
            node.execute();
            if (!node.success())
                throw new RuntimeException("Workflow node " + node.getIdentifier() + " did not succeed");
        }
        //execute successor nodes, if have not been executed yet
        for (IWorkflowNode successor : node.getSuccessors()) {
            if (!successor.success())
                executeNode(successor);
        }
    }

}
