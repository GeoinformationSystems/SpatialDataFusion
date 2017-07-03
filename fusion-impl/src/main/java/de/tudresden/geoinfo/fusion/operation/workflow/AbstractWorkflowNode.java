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

    private Map<String, IIdentifier> inputTitles = new HashMap<>();
    private Map<IIdentifier, IInputConnector> inputConnectors = new HashMap<>();
    private Map<String, IIdentifier> outputTitles = new HashMap<>();
    private Map<IIdentifier, IOutputConnector> outputConnectors = new HashMap<>();

    public AbstractWorkflowNode(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description){
        super(identifier, title, description);
        this.initializeConnectors();
    }

    /**
     * initialize IO connectors
     */
    protected void initializeConnectors() {
        //set input connectors
        this.clearInputConnectors();
        initializeInputConnectors();
        //set output connectors
        this.clearOutputConnectors();
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

    @Override
    @NotNull
    public IInputConnector getInputConnector(@NotNull IIdentifier identifier) {
        if(!this.inputConnectors.containsKey(identifier))
            throw new IllegalArgumentException("Input " + identifier + " is not defined");
        return this.inputConnectors.get(identifier);
    }

    /**
     * get input connector
     * @param title connector title
     * @param includeIdentifier flag: include identifier.toString() in search
     * @return connector associated with title
     */
    public @NotNull IInputConnector getInputConnector(@NotNull String title, boolean includeIdentifier) {
        //check for identifier
        if(includeIdentifier){
            for(IIdentifier identifier : this.getInputIdentifiers()){
                if(identifier.toString().equals(title))
                    return this.getInputConnector(identifier);
            }
        }
        //check for title
        return this.getInputConnector(this.getInputIdentifier(title));
    }

    @NotNull
    public IInputConnector getInputConnector(@NotNull String title) {
        return this.getInputConnector(title, true);
    }

    @NotNull
    public Collection<IIdentifier> getInputIdentifiers() {
        return this.inputTitles.values();
    }

    @NotNull
    public IIdentifier getInputIdentifier(@NotNull String title) {
        if(!this.inputTitles.containsKey(title))
            throw new IllegalArgumentException("Input " + title + " is not defined");
        return this.inputTitles.get(title);
    }

    /**
     * adds an input connector
     *
     * @param connector workflow connector
     */
    public void addConnector(@NotNull IWorkflowConnector connector) {
        if(connector instanceof IInputConnector)
            this.addInputConnector((IInputConnector) connector);
        else if(connector instanceof IOutputConnector)
            this.addOutputConnector((IOutputConnector) connector);
    }

    private void addInputConnector(@NotNull IInputConnector inputConnector, int index) {
        String title = index == 0 ? getConnectorTitle(inputConnector) : getConnectorTitle(inputConnector) + "_" + index;
        if (this.inputTitles.containsKey(title))
            addInputConnector(inputConnector, ++index);
        else {
            this.inputTitles.put(title, inputConnector.getIdentifier());
            this.inputConnectors.put(inputConnector.getIdentifier(), inputConnector);
        }
    }

    /**
     * adds an input connector
     *
     * @param inputConnector input connector
     */
    public void addInputConnector(@NotNull IInputConnector inputConnector) {
        this.addInputConnector(inputConnector, 0);
    }

    private String getConnectorTitle(IWorkflowConnector connector){
        return connector.getTitle() != null ? connector.getTitle() : connector.getIdentifier().toString();
    }

    /**
     * adds an input connector
     *
     * @param identifier            input connector identifier
     * @param title                 input connector title
     * @param description           input connector description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(new InputConnector(identifier, title, description, this, runtimeConstraints, connectionConstraints, defaultData));
    }

    /**
     * adds an input connector
     *
     * @param identifier            input connector identifier
     * @param title                 input connector title
     * @param description           input connector description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(identifier, title, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null, defaultData);
    }

    @NotNull
    @Override
    public Collection<IOutputConnector> getOutputConnectors() {
        return this.outputConnectors.values();
    }

    @Override
    @NotNull
    public IOutputConnector getOutputConnector(@NotNull IIdentifier identifier) {
        if(!this.outputConnectors.containsKey(identifier))
            throw new IllegalArgumentException("Output " + identifier + " is not defined");
        return this.outputConnectors.get(identifier);
    }

    /**
     * get output connector
     * @param title connector title
     * @param includeIdentifier flag: include identifier.toString() in search
     * @return connector associated with title
     */
    public @NotNull IOutputConnector getOutputConnector(@NotNull String title, boolean includeIdentifier) {
        //check for identifier
        if(includeIdentifier){
            for(IIdentifier identifier : this.getOutputIdentifiers()){
                if(identifier.toString().equals(title))
                    return this.getOutputConnector(identifier);
            }
        }
        //check for title
        return this.getOutputConnector(this.getOutputIdentifier(title));
    }

    @NotNull
    public IOutputConnector getOutputConnector(@NotNull String title) {
        return this.getOutputConnector(title, true);
    }

    @NotNull
    public Collection<IIdentifier> getOutputIdentifiers() {
        return this.outputTitles.values();
    }

    @NotNull
    public IIdentifier getOutputIdentifier(@NotNull String title) {
        if(!this.outputTitles.containsKey(title))
            throw new IllegalArgumentException("Output " + title + " is not defined");
        return this.outputTitles.get(title);
    }

    private void addOutputConnector(@NotNull IOutputConnector outputConnector, int index) {
        String title = index == 0 ? getConnectorTitle(outputConnector) : getConnectorTitle(outputConnector) + "_" + index;
        if (this.outputTitles.containsKey(title))
            addOutputConnector(outputConnector, ++index);
        else {
            this.outputTitles.put(title, outputConnector.getIdentifier());
            this.outputConnectors.put(outputConnector.getIdentifier(), outputConnector);
        }
    }

    /**
     * adds an output connector
     *
     * @param outputConnector output connector
     */
    public void addOutputConnector(@NotNull IOutputConnector outputConnector) {
        this.addOutputConnector(outputConnector, 0);
    }

    /**
     * adds an input connector
     *
     * @param identifier            input connector identifier
     * @param title                 input connector title
     * @param description           input connector description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints) {
        this.addOutputConnector(new OutputConnector(identifier, title, description, this, runtimeConstraints, connectionConstraints));
    }

    /**
     * adds an input connector
     *
     * @param identifier            input connector identifier
     * @param title                 input connector title
     * @param description           input connector description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        this.addOutputConnector(identifier, title, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null);
    }

    @Override
    public boolean isReady() {
        //set ready, if all input connectors have been configured successfully
        for (IInputConnector inputConnector : this.getInputConnectors()) {
            if (!inputConnector.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSuccess() {
        //set success if all output connectors have been configured successfully
        for (IOutputConnector output : this.getOutputConnectors()) {
            if (!output.isReady()) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public Collection<IWorkflowNode> getAncestors() {
        Set<IWorkflowNode> ancestors = new HashSet<>();
        for (IInputConnector input : this.getInputConnectors()) {
            for (IWorkflowConnection connection : input.getConnections()) {
                ancestors.add(connection.getOutputConnector().getEntity());
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
                successors.add(connection.getInputConnector().getEntity());
            }
        }
        return successors;
    }

    /**
     * get input data from selected connector
     *
     * @param identifier connector identifier
     * @return data associated with connector
     */
    public @NotNull IData getInputData(@NotNull IIdentifier identifier) {
        return getInputData(getInputConnector(identifier));
    }

    /**
     * get input data from selected connector
     *
     * @param title connector title
     * @return data associated with connector
     */
    @NotNull
    protected IData getInputData(@NotNull String title) {
        return getInputData(getInputConnector(title));
    }

    /**
     * get input data from connector
     *
     * @param connector input connector
     * @return data associated with connector (can be null)
     */
    private @NotNull IData getInputData(@NotNull IInputConnector connector) {
        if (connector.getData() == null)
            throw new RuntimeException("Data attached to connector is null");
        return connector.getData();
    }

    /**
     * remove an output connector
     *
     * @param identifier connector identifier
     */
    protected void removeOutputConnector(@NotNull IIdentifier identifier) {
        this.outputConnectors.remove(identifier);
    }

    /**
     * remove all output connectors
     */
    protected void clearOutputConnectors() {
        this.outputConnectors.clear();
        this.outputTitles.clear();
    }

    /**
     * remove all output connectors
     */
    protected void clearInputConnectors() {
        this.inputConnectors.clear();
        this.inputTitles.clear();
    }

    /**
     * get output data from selected connector
     *
     * @param identifier connector identifier
     * @return data associated with connector
     */
    public @Nullable IData getOutputData(@NotNull IIdentifier identifier) {
        return getOutputData(getOutputConnector(identifier));
    }

    /**
     * get output data from selected connector
     *
     * @param title connector title
     * @return data associated with connector
     */
    public @Nullable IData getOutputData(@NotNull String title) {
        return getOutputData(getOutputConnector(title));
    }

    /**
     * get output data from connector
     *
     * @param connector output connector
     * @return data associated with connector (can be null)
     */
    private @Nullable IData getOutputData(@Nullable IOutputConnector connector) {
        if (connector == null)
            throw new IllegalArgumentException("Requested connector is not available");
        return connector.getData();
    }

    /**
     * set input connectors
     *
     * @param inputs process inputs
     */
    public void connectInputs(@NotNull Map<IIdentifier, IData> inputs) {
        for (Map.Entry<IIdentifier, IData> input : inputs.entrySet()) {
            if (input.getValue() != null)
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
        this.connectData(connector, input);
    }

    /**
     * connect input
     *
     * @param title connector title
     * @param input input data
     */
    public void connectInput(@NotNull String title, @Nullable IData input) {
        IInputConnector connector = this.getInputConnector(title);
        if (connector != null)
            this.connectData(connector, input);
    }

    /**
     * connect input
     *
     * @param connector connector
     * @param data      data
     */
    protected void connectData(@NotNull IWorkflowConnector connector, @Nullable IData data) {
        connector.setData(data);
    }

    /**
     * connect output
     *
     * @param identifier connector identifier
     * @param output     output data
     */
    public void connectOutput(@NotNull IIdentifier identifier, @Nullable IData output) {
        IOutputConnector connector = this.getOutputConnector(identifier);
        if (connector != null)
            this.connectData(connector, output);
    }

    /**
     * connect output
     *
     * @param title  connector title
     * @param output output data
     */
    public void connectOutput(@NotNull String title, @Nullable IData output) {
        IOutputConnector connector = this.getOutputConnector(title);
        if (connector != null)
            this.connectData(connector, output);
    }

    /**
     * reset input and output connections
     */
    public void reset() {
        for(IInputConnector inputConnector : this.getInputConnectors()){
            inputConnector.reset();
        }
        for(IOutputConnector outputConnector : this.getOutputConnectors()){
            outputConnector.reset();
        }
    }

}
