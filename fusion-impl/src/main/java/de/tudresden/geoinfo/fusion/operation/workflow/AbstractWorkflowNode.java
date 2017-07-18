package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * workflow element implementation
 */
public abstract class AbstractWorkflowNode extends AbstractWorkflowElement implements IWorkflowNode {

    private Map<IIdentifier, IInputConnector> inputConnectors = new HashMap<>();
    private Map<IIdentifier, IOutputConnector> outputConnectors = new HashMap<>();
    private Map<String, IIdentifier> localInputIdentifiers = new HashMap<>();
    private Map<String, IIdentifier> localOutputIdentifiers = new HashMap<>();

    /**
     * constructor
     * @param identifier element identifier
     */
    public AbstractWorkflowNode(@NotNull IIdentifier identifier, String description){
        super(identifier, description);
        this.initializeConnectors();
    }

    /**
     * initialize IO connectors
     */
    protected void initializeConnectors() {
        this.clearConnectors();
        this.initializeInputConnectors();
        this.initializeOutputConnectors();
    }

    /**
     * initialize input connectors, must use the provided methods addInputConnector(...)
     */
    protected abstract void initializeInputConnectors();

    /**
     * initialize output connectors, must use the provided methods addOutputConnector(...)
     */
    protected abstract void initializeOutputConnectors();

    /**
     * get connector by identifier
     * @param connectors set of connectors
     * @param identifier identifier to search for
     * @return connector or null, if identifier is not found
     */
    @NotNull
    private <T extends IWorkflowConnector> T getConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull IIdentifier identifier) {
        if(!connectors.containsKey(identifier))
            throw new IllegalArgumentException("connector with identifier " + identifier.getGlobalIdentifier() + " does not exist");
        return connectors.get(identifier);
    }

    /**
     * get connector by local identifier
     * @param connectors set of connectors
     * @param localIdentifier local identifier to search for
     * @return connector or null, if identifier is not found
     */
    @NotNull
    private <T extends IWorkflowConnector> T getConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier) {
        if(!localIdentifiers.containsKey(localIdentifier))
            throw new IllegalArgumentException("connector with local identifier " + localIdentifier + " does not exist");
        return connectors.get(localIdentifiers.get(localIdentifier));
    }

    /**
     * add a connector
     * @param connectors connectors
     * @param localIdentifiers local identifiers
     * @param connector connector to add
     * @param index current index for local identifier
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void addConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull T connector, int index) {
        //do nothing if connector is already present
        if(connectors.containsKey(connector.getIdentifier()))
            return;
        //check for local identifier, increase index if local identifier already exists
        String localIdentifier = connector.getIdentifier().getLocalIdentifier() + (index > 0 ? "_" + index : "");
        if(localIdentifiers.containsKey(localIdentifier))
            this.addConnector(connectors, localIdentifiers, connector, ++index);
        else {
            if(index > 0)
                connector.getIdentifier().setLocalIdentifier(localIdentifier);
            connectors.put(connector.getIdentifier(), connector);
            localIdentifiers.put(localIdentifier, connector.getIdentifier());
        }
    }

    /**
     * add connector to node, modifies local identifier (localIdentifier + _index), if duplicate identifiers exist
     * @param connectors set of existing connectors
     * @param localIdentifiers local identifiers
     * @param connector connector to be added
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void addConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull T connector) {
        this.addConnector(connectors, localIdentifiers, connector, 0);
    }

    /**
     * remove connector
     * @param connectors set of existing connectors
     * @param localIdentifiers local identifiers
     * @param identifier connector identifier
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void removeConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull IIdentifier identifier) {
        connectors.remove(identifier);
        localIdentifiers.remove(identifier.getLocalIdentifier());
    }

    /**
     * remove connector
     * @param connectors set of existing connectors
     * @param localIdentifiers local identifiers
     * @param localIdentifier local connector identifier
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void removeConnector(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier) {
        if(!localIdentifiers.containsKey(localIdentifier))
            return;
        this.removeConnector(connectors, localIdentifiers, localIdentifiers.get(localIdentifier));
    }

    /**
     * get connector id from local identifer
     * @param localIdentifiers set of identifiers
     * @param localIdentifier local connector identifier
     * @return connector id by local identifier
     */
    private @NotNull IIdentifier getIdentifier(@NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier) {
        if(!localIdentifiers.containsKey(localIdentifier))
            throw new IllegalArgumentException("connector with local identifier " + localIdentifier + " does not exist");
        return localIdentifiers.get(localIdentifier);
    }

    /**
     * get output data from selected connector
     * @param connectors set of existing connectors
     * @param identifier connector identifier
     * @return data associated with connector
     */
    private @Nullable <T extends IWorkflowConnector> IData getData(@NotNull Map<IIdentifier,T> connectors, @NotNull IIdentifier identifier) {
        return connectors.get(identifier).getData();
    }

    /**
     * get mandatory data from selected connector
     * @param connectors set of existing connectors
     * @param identifier connector identifier
     * @return data associated with connector
     */
    private @NotNull <T extends IWorkflowConnector> IData getMandatoryData(@NotNull Map<IIdentifier,T> connectors, @NotNull IIdentifier identifier) {
        IData data = connectors.get(identifier).getData();
        if(data == null)
            throw new RuntimeException("Mandatory constraint violation for identifier " + identifier.getLocalIdentifier());
        return data;
    }

    /**
     * get data from selected connector
     *
     * @param connectors set of existing connectors
     * @param localIdentifier local connector identifier
     * @return data associated with connector or null, if connector does not exist
     */
    private @Nullable <T extends IWorkflowConnector> IData getData(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier) {
        if(!localIdentifiers.containsKey(localIdentifier))
            throw new IllegalArgumentException("connector with local identifier " + localIdentifier + " does not exist");
        return getData(connectors, localIdentifiers.get(localIdentifier));
    }

    /**
     * get mandatory data from selected connector
     *
     * @param connectors set of existing connectors
     * @param localIdentifier local connector identifier
     * @return data associated with connector or null, if connector does not exist
     */
    private @NotNull <T extends IWorkflowConnector> IData getMandatoryData(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier) {
        IData data = this.getData(connectors, localIdentifiers, localIdentifier);
        if(data == null)
            throw new RuntimeException("Mandatory constraint violation for identifier " + localIdentifier);
        return data;
    }

    /**
     * connect data to corresponding connector
     *
     * @param connector connector
     * @param data      data
     */
    private <T extends IWorkflowConnector> void setData(@NotNull T connector, @Nullable IData data) {
        connector.setData(data);
    }

    /**
     * connect data to corresponding connector
     * @param connectors set of connectors
     * @param identifier connector identifier
     * @param data data object
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void setData(@NotNull Map<IIdentifier,T> connectors, @NotNull IIdentifier identifier, @Nullable IData data) {
        this.setData(this.getConnector(connectors, identifier), data);
    }

    /**
     * connect data to corresponding connector
     * @param connectors set of connectors
     * @param localIdentifier local connector identifier
     * @param data data object
     * @param <T> type of connector
     */
    private <T extends IWorkflowConnector> void setData(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull String localIdentifier, @Nullable IData data) {
        this.setData(this.getConnector(connectors, localIdentifiers, localIdentifier), data);
    }

    /**
     * connect data to corresponding connectors
     *
     * @param connectors set of connectors
     * @param localIdentifiers local identifiers
     * @param data data map
     */
    private <T extends IWorkflowConnector> void setData(@NotNull Map<IIdentifier,T> connectors, @NotNull Map<String,IIdentifier> localIdentifiers, @NotNull Map<String,IData> data) {
        for (Map.Entry<String, IData> input : data.entrySet()) {
            if (input.getValue() != null)
                this.setData(connectors, localIdentifiers, input.getKey(), input.getValue());
        }
    }

    @NotNull
    @Override
    public Collection<IInputConnector> getInputConnectors() {
        return this.inputConnectors.values();
    }

    @NotNull
    @Override
    public Collection<IOutputConnector> getOutputConnectors() {
        return this.outputConnectors.values();
    }

    @Override
    @NotNull
    public IInputConnector getInputConnector(@NotNull IIdentifier identifier) {
        return this.getConnector(this.inputConnectors, identifier);
    }

    @Override
    @NotNull
    public IOutputConnector getOutputConnector(@NotNull IIdentifier identifier) {
        return this.getConnector(this.outputConnectors, identifier);
    }

    @Override
    @NotNull
    public IInputConnector getInputConnector(@NotNull String localIdentifier) {
        return this.getConnector(this.inputConnectors, this.localInputIdentifiers, localIdentifier);
    }

    @Override
    @NotNull
    public IOutputConnector getOutputConnector(@NotNull String localIdentifier) {
        return this.getConnector(this.outputConnectors, this.localOutputIdentifiers, localIdentifier);
    }

    /**
     * adds an input connector
     *
     * @param inputConnector input connector
     */
    public void addInputConnector(@NotNull IInputConnector inputConnector) {
        this.addConnector(this.inputConnectors, this.localInputIdentifiers, inputConnector);
    }

    /**
     * adds an output connector
     *
     * @param outputConnector output connector
     */
    public void addOutputConnector(@NotNull IOutputConnector outputConnector) {
        this.addConnector(this.outputConnectors, this.localOutputIdentifiers, outputConnector);
    }

    /**
     * adds an input connector
     *
     * @param identifier element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@NotNull IIdentifier identifier, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(new InputConnector(identifier, description, this, runtimeConstraints, connectionConstraints, defaultData));
    }

    /**
     * adds an input connector
     *
     * @param identifier element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@NotNull IIdentifier identifier, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(identifier, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null, defaultData);
    }

    /**
     * adds an input connector
     *
     * @param localIdentifier local element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@NotNull String localIdentifier, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(new ResourceIdentifier(null, localIdentifier), description, runtimeConstraints, connectionConstraints, defaultData);
    }

    /**
     * adds an input connector
     *
     * @param localIdentifier local element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     * @param defaultData           default data object
     */
    public void addInputConnector(@NotNull String localIdentifier, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints, @Nullable IData defaultData) {
        this.addInputConnector(new ResourceIdentifier(null, localIdentifier), description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null, defaultData);
    }

    /**
     * adds an input connector
     *
     * @param identifier element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@NotNull IIdentifier identifier, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints) {
        this.addOutputConnector(new OutputConnector(identifier, description, this, runtimeConstraints, connectionConstraints));
    }

    /**
     * adds an input connector
     *
     * @param identifier element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@NotNull IIdentifier identifier, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        this.addOutputConnector(identifier, description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null);
    }

    /**
     * adds an input connector
     *
     * @param localIdentifier local element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@NotNull String localIdentifier, @Nullable String description, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints) {
        this.addOutputConnector(new ResourceIdentifier(null, localIdentifier), description, runtimeConstraints, connectionConstraints);
    }

    /**
     * adds an input connector
     *
     * @param localIdentifier local element identifier
     * @param description element description
     * @param runtimeConstraints    existing data constraints
     * @param connectionConstraints existing connection constraints
     */
    public void addOutputConnector(@NotNull String localIdentifier, @Nullable String description, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        this.addOutputConnector(new ResourceIdentifier(null, localIdentifier), description, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null);
    }

    @Override
    public boolean ready() {
        //set ready, if all input connectors have been configured successfully
        for (IInputConnector inputConnector : this.getInputConnectors()) {
            if (!inputConnector.ready()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean success() {
        //set success if all output connectors have been configured successfully
        for (IOutputConnector outputConnector : this.getOutputConnectors()) {
            if (!outputConnector.ready()) {
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
     * get input connector id from local identifier
     * @param localIdentifier local connector identifier
     * @return input connector id
     */
    public @NotNull IIdentifier getInputIdentifier(@NotNull String localIdentifier) {
        return this.getIdentifier(this.localInputIdentifiers, localIdentifier);
    }

    /**
     * get output connector id from local identifier
     * @param localIdentifier local connector identifier
     * @return output connector id
     */
    public @NotNull IIdentifier getOutputIdentifier(@NotNull String localIdentifier) {
        return this.getIdentifier(this.localOutputIdentifiers, localIdentifier);
    }

    /**
     * get input data from selected connector
     *
     * @param localIdentifier local connector identifier
     * @return data associated with connector or null, if connector does not exist
     */
    public @Nullable IData getInputData(@NotNull String localIdentifier) {
        return this.getData(this.inputConnectors, this.localInputIdentifiers, localIdentifier);
    }

    /**
     * get mandatory input data from selected connector
     *
     * @param localIdentifier local connector identifier
     * @return data associated with connector
     * @throws RuntimeException if data is null
     */
    public @NotNull IData getMandatoryInputData(@NotNull String localIdentifier) {
        return this.getMandatoryData(this.inputConnectors, this.localInputIdentifiers, localIdentifier);
    }

    /**
     * get output data from selected connector
     * @param identifier connector identifier
     * @return data associated with connector
     */
    public @Nullable IData getInputData(@NotNull IIdentifier identifier) {
        return this.getData(this.inputConnectors, identifier);
    }

    /**
     * get input data from selected connector
     *
     * @param localIdentifier local connector identifier
     * @return data associated with connector or null, if connector does not exist
     */
    public @Nullable IData getOutputData(@NotNull String localIdentifier) {
        return this.getData(this.outputConnectors, this.localOutputIdentifiers, localIdentifier);
    }

    /**
     * get mandatory output data from selected connector
     *
     * @param localIdentifier local connector identifier
     * @return data associated with connector
     * @throws RuntimeException if data is null
     */
    public @NotNull IData getMandatoryOutputData(@NotNull String localIdentifier) {
        return this.getMandatoryData(this.outputConnectors, this.localOutputIdentifiers, localIdentifier);
    }

    /**
     * get output data from selected connector
     * @param identifier connector identifier
     * @return data associated with connector
     */
    public @Nullable IData getOutputData(@NotNull IIdentifier identifier) {
        return this.getData(this.outputConnectors, identifier);
    }

    /**
     * remove an output connector
     *
     * @param identifier connector identifier
     */
    protected void removeInputConnector(@NotNull IIdentifier identifier) {
        this.removeConnector(this.inputConnectors, this.localInputIdentifiers, identifier);
    }

    /**
     * remove an output connector
     *
     * @param localIdentifier local connector identifier
     */
    protected void removeInputConnector(@NotNull String localIdentifier) {
        this.removeConnector(this.inputConnectors, this.localInputIdentifiers, localIdentifier);
    }

    /**
     * remove an output connector
     *
     * @param identifier connector identifier
     */
    protected void removeOutputConnector(@NotNull IIdentifier identifier) {
        this.removeConnector(this.outputConnectors, this.localOutputIdentifiers, identifier);
    }

    /**
     * remove an output connector
     *
     * @param localIdentifier local connector identifier
     */
    protected void removeOutputConnector(@NotNull String localIdentifier) {
        this.removeConnector(this.outputConnectors, this.localOutputIdentifiers, localIdentifier);
    }

    /**
     * clear connectors
     */
    protected void clearConnectors() {
        this.clearInputConnectors();
        this.clearOutputConnectors();
    }

    /**
     * clear output connectors
     */
    protected void clearInputConnectors() {
        this.inputConnectors.clear();
        this.localInputIdentifiers.clear();
    }

    /**
     * clear output connectors
     */
    protected void clearOutputConnectors() {
        this.outputConnectors.clear();
        this.localOutputIdentifiers.clear();
    }

    /**
     * set input connectors
     *
     * @param inputs process inputs
     */
    public void setInputs(@NotNull Map<IIdentifier, IData> inputs) {
        for (Map.Entry<IIdentifier, IData> input : inputs.entrySet()) {
            if (input.getValue() != null)
                this.setInput(input.getKey(), input.getValue());
        }
    }

    /**
     * connect input
     *
     * @param identifier connector identifier
     * @param data input data
     */
    public void setInput(@NotNull IIdentifier identifier, @Nullable IData data) {
        this.setData(this.inputConnectors, identifier, data);
    }

    /**
     * connect input
     *
     * @param localIdentifier local connector identifier
     * @param data input data
     */
    public void setInput(@NotNull String localIdentifier, @Nullable IData data) {
        this.setData(this.inputConnectors, this.localInputIdentifiers, localIdentifier, data);
    }

    /**
     * set output connectors
     *
     * @param outputs process outputs
     */
    public void setOutputs(@NotNull Map<IIdentifier, IData> outputs) {
        for (Map.Entry<IIdentifier, IData> output : outputs.entrySet()) {
            if (output.getValue() != null)
                this.setOutput(output.getKey(), output.getValue());
        }
    }

    /**
     * connect output
     *
     * @param identifier connector identifier
     * @param data output data
     */
    public void setOutput(@NotNull IIdentifier identifier, @Nullable IData data) {
        this.setData(this.outputConnectors, identifier, data);
    }

    /**
     * connect input
     *
     * @param localIdentifier local connector identifier
     * @param data input data
     */
    public void setOutput(@NotNull String localIdentifier, @Nullable IData data) {
        this.setData(this.outputConnectors, this.localOutputIdentifiers, localIdentifier, data);
    }

    /**
     * reset input and output connectors
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
