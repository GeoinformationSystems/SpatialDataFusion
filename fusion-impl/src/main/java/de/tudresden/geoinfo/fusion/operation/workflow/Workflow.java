package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * standard workflow operation
 */
public class Workflow extends AbstractOperation implements IWorkflow {

    private static final String PROCESS_TITLE = Workflow.class.getSimpleName();

    private Collection<IWorkflowNode> nodes;

    /**
     * empty constructor, if used initializeConnectors() must be called after setting workflow nodes
     */
    Workflow() {
        super(PROCESS_TITLE, false);
    }

    /**
     * Constructor
     *
     * @param nodes workflow nodes
     */
    public Workflow(Collection<IWorkflowNode> nodes) {
        this();
        this.setNodes(nodes);
    }

    @Override
    protected void initializeInputConnectors() {
        for (IWorkflowNode node : this.getWorkflowNodes()) {
            for (IInputConnector connector : node.getInputConnectors()) {
                if (connector.getConnections().isEmpty())
                    this.addInputConnector(connector);
            }
        }
    }

    @Override
    protected void initializeOutputConnectors() {
        for (IWorkflowNode node : this.getWorkflowNodes()) {
            for (IOutputConnector connector : node.getOutputConnectors()) {
                if (connector.getConnections().isEmpty())
                    this.addOutputConnector(connector);
            }
        }
    }

    @Override
    public void execute() {
        executeNode(this.nodes.iterator().next());
    }

    @NotNull
    @Override
    public Collection<IWorkflowNode> getWorkflowNodes() {
        return this.nodes;
    }

    @Override
    public @NotNull Set<IWorkflowConnection> getWorkflowConnections() {
        Set<IWorkflowConnection> connections = new HashSet<>();
        for(IWorkflowNode node : this.getWorkflowNodes()){
            for(IInputConnector inConnector : node.getInputConnectors()){
                connections.addAll(inConnector.getConnections());
            }
            for(IOutputConnector outConnector : node.getOutputConnectors()){
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
    protected void addNode(IWorkflowNode node) {
        if (this.nodes == null)
            this.nodes = new HashSet<>();
        this.nodes.add(node);
        initializeConnectors();
    }

    /**
     * set workflow nodes
     *
     * @param nodes input workflow nodes
     */
    protected void setNodes(Collection<IWorkflowNode> nodes) {
        this.nodes = nodes;
        initializeConnectors();
    }

    /**
     * recursively execute nodes (execute ancestor nodes first, then perform operation, finally call successor node)
     *
     * @param node node to execute
     * @return true, if node has been executed
     */
    public void executeNode(IWorkflowNode node) {
        //execute ancestors and self, if this node has not been executed yet
        if (!node.getState().equals(ElementState.SUCCESS)) {
            //execute ancestors, if have not been executed yet
            for (IWorkflowNode ancestor : node.getAncestors()) {
                if (!ancestor.getState().equals(ElementState.SUCCESS))
                    executeNode(ancestor);
            }
            //execute self
            node.performAction();
            if (!node.getState().equals(ElementState.SUCCESS))
                throw new RuntimeException("Workflow node " + node.getIdentifier() + " did not succeed");
        }
        //execute successor nodes, if have not been executed yet
        for (IWorkflowNode successor : node.getSuccessors()) {
            if (!successor.getState().equals(ElementState.SUCCESS))
                executeNode(successor);
        }
    }
}
