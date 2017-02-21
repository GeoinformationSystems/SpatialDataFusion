package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * workflow element implementation
 */
public abstract class AbstractWorkflowNode extends AbstractWorkflowElement implements IWorkflowNode {

    private Map<IIdentifier, IInputConnector> inputConnectors;
    private Map<IIdentifier, IOutputConnector> outputConnectors;

    /**
     * constructor
     *
     * @param identifier node identifier
     * @param initialize flag: initialize inputs and outputs (if false, initialization must explicitly be invoked by implementing class)
     */
    public AbstractWorkflowNode(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, boolean initialize) {
        super(identifier, title, description);
        if (initialize)
            initializeConnectors();
    }

    /**
     * constructor
     *
     * @param identifier node identifier
     */
    public AbstractWorkflowNode(@Nullable IIdentifier identifier) {
        this(identifier, null, null, true);
    }

    /**
     * constructor
     *
     * @param title node title
     */
    public AbstractWorkflowNode(@Nullable String title) {
        this(null, title, null, true);
    }

    /**
     * initialize IO connectors
     */
    protected void initializeConnectors() {
        //set input connectors
        if (this.inputConnectors == null)
            this.inputConnectors = new HashMap<>();
        else
            this.inputConnectors.clear();
        initializeInputConnectors();
        //set output connectors
        if (this.outputConnectors == null)
            this.outputConnectors = new HashMap<>();
        else
            this.outputConnectors.clear();
        outputConnectors = new HashMap<>();
        initializeOutputConnectors();
    }

    /**
     * initialize input connectors, must use the provided methods addInputConnector(...)
     */
    protected abstract void initializeInputConnectors();

    /**
     * initialize output connectors, must use the provided methods addOutputConnector(...)
     */
    protected abstract void initializeOutputConnectors();

    @NotNull
    @Override
    public Collection<IInputConnector> getInputConnectors() {
        return this.inputConnectors.values();
    }

    @NotNull
    @Override
    public Set<IIdentifier> getInputIdentifiers() {
        return this.inputConnectors.keySet();
    }

    @Override
    @Nullable
    public IInputConnector getInputConnector(@NotNull IIdentifier identifier) {
        return this.inputConnectors.get(identifier);
    }

    @Override
    public void updateState() {
        //set ready, if all input connectors indicate success
        for (IInputConnector input : this.getInputConnectors()) {
            if (!input.getState().equals(ElementState.SUCCESS)) {
                return;
            }
        }
        this.setState(ElementState.READY);
        //set success if all output connectors indicate success
        for (IOutputConnector output : this.getOutputConnectors()) {
            if (!output.getState().equals(ElementState.SUCCESS)) {
                return;
            }
        }
        this.setState(ElementState.SUCCESS);
    }

    @NotNull
    @Override
    public Collection<IWorkflowNode> getAncestors() {
        Set<IWorkflowNode> ancestors = new HashSet<>();
        for (IInputConnector input : this.getInputConnectors()) {
            for (IWorkflowConnection connection : input.getConnections()) {
                ancestors.add(connection.getOutput().getEntity());
            }
        }
        return ancestors;
    }

    @NotNull
    @Override
    public Collection<IWorkflowNode> getSuccessors() {
        Set<IWorkflowNode> successors = new HashSet<>();
        for (IOutputConnector output : this.getOutputConnectors()) {
            for (IWorkflowConnection connection : output.getConnections()) {
                successors.add(connection.getInput().getEntity());
            }
        }
        return successors;
    }

    /**
     * get input connector by title
     *
     * @param title connector title
     * @return corresponding input connector
     */
    public IInputConnector getInputConnector(String title) {
        for (IInputConnector connector : this.getInputConnectors()) {
            if (connector.getTitle().equals(title))
                return connector;
        }
        return null;
    }

    /**
     * adds an input connector
     *
     * @param inputConnector input connector
     */
    public void addInputConnector(@NotNull IInputConnector inputConnector) {
        this.inputConnectors.put(inputConnector.getIdentifier(), inputConnector);
    }

    /**
     * add an input connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     * @param defaultConnector      default data object
     */
    public void addInputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints, @Nullable IOutputConnector defaultConnector) {
        this.addInputConnector(new InputConnector(identifier, title, description, this, runtimeConstraints, connectionConstraints, defaultConnector));
    }

    /**
     * add an input connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     * @param defaultConnector      default data object
     */
    public void addInputConnector(@Nullable String title, @Nullable String description, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints, @Nullable IOutputConnector defaultConnector) {
        this.addInputConnector(null, title, description, runtimeConstraints, connectionConstraints, defaultConnector);
    }

    /**
     * add an input connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     * @param defaultConnector      default data object
     */
    public void addInputConnector(@Nullable String title, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints, @Nullable IOutputConnector defaultConnector) {
        this.addInputConnector(title, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null, defaultConnector);
    }

    @NotNull
    @Override
    public Collection<IOutputConnector> getOutputConnectors() {
        return this.outputConnectors.values();
    }

    @NotNull
    @Override
    public Set<IIdentifier> getOutputIdentifiers() {
        return this.outputConnectors.keySet();
    }

    @Override
    public @Nullable IOutputConnector getOutputConnector(@NotNull IIdentifier identifier) {
        return this.outputConnectors.get(identifier);
    }

    /**
     * get output connector by simple identifier
     *
     * @param title connector title
     * @return corresponding output connector
     */
    public IOutputConnector getOutputConnector(String title) {
        for (IOutputConnector connector : this.getOutputConnectors()) {
            if (connector.getTitle().equals(title))
                return connector;
        }
        return null;
    }

    /**
     * adds an output connector
     *
     * @param outputConnector output connector
     */
    public void addOutputConnector(@NotNull IOutputConnector outputConnector) {
        this.outputConnectors.put(outputConnector.getIdentifier(), outputConnector);
    }

    /**
     * add an output connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     */
    public void addOutputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this.addOutputConnector(new OutputConnector(identifier, title, description, this, runtimeConstraints, connectionConstraints));
    }

    /**
     * add an output connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     */
    public void addOutputConnector(@Nullable String title, @Nullable String description, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this.addOutputConnector(null, title, description, runtimeConstraints, connectionConstraints);
    }

    /**
     * add an output connector to this operation
     *
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing metadata constraints
     */
    public void addOutputConnector(@Nullable String title, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        this.addOutputConnector(title, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null);
    }

    /**
     * set input connectors
     *
     * @param inputs process inputs
     */
    public void connectInputs(Map<IIdentifier, IData> inputs) {
        for (Map.Entry<IIdentifier, IData> input : inputs.entrySet()) {
            this.connectInput(input.getKey(), input.getValue());
        }
    }

    /**
     * connect input
     *
     * @param identifier connector identifier
     * @param input      input data
     */
    public void connectInput(@NotNull IIdentifier identifier, @NotNull IData input) {
        IInputConnector connector = this.getInputConnector(identifier);
        if (connector != null) {
            connector.setData(input);
        }
    }

    /**
     * connect input
     *
     * @param title connector title
     * @param input input data
     */
    public void connectInput(@NotNull String title, @NotNull IData input) {
        IInputConnector connector = this.getInputConnector(title);
        if (connector != null) {
            connector.setData(input);
        }
    }

    /**
     * connect output
     *
     * @param identifier connector identifier
     * @param output     output data
     */
    public void connectOutput(@NotNull IIdentifier identifier, @NotNull IData output) {
        IOutputConnector connector = this.getOutputConnector(identifier);
        if (connector != null) {
            connector.setData(output);
        }
    }

    /**
     * connect output
     *
     * @param title  connector title
     * @param output output data
     */
    public void connectOutput(@NotNull String title, @NotNull IData output) {
        IOutputConnector connector = this.getOutputConnector(title);
        if (connector != null) {
            connector.setData(output);
        }
    }

}
