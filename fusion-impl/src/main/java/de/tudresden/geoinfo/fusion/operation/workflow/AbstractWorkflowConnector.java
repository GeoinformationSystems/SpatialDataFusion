package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * IO connector implementation
 */
public abstract class AbstractWorkflowConnector extends AbstractWorkflowElement implements IWorkflowConnector {

    private IWorkflowNode entity;
    private Collection<IWorkflowConnection> connections;
    private Collection<IRuntimeConstraint> runtimeConstraints;
    private Collection<IConnectionConstraint> connectionConstraints;
    private IData data;

    /**
     * constructor
     * @param identifier element identifier
     * @param description element description
     * @param entity                associated entity
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints) {
        super(identifier, description);
        this.entity = entity;
        this.connections = new HashSet<>();
        this.runtimeConstraints = runtimeConstraints != null ? runtimeConstraints : new HashSet<>();
        this.connectionConstraints = connectionConstraints != null ? connectionConstraints : new HashSet<>();
    }

    /**
     * constructor
     * @param identifier element identifier
     * @param description element description
     * @param entity                associated entity
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        this(identifier, description, entity, runtimeConstraints != null ? new HashSet<>(Arrays.asList(runtimeConstraints)) : null, connectionConstraints != null ? new HashSet<>(Arrays.asList(connectionConstraints)) : null);
    }

    @NotNull
    @Override
    public Collection<IRuntimeConstraint> getRuntimeConstraints() {
        return runtimeConstraints;
    }

    @NotNull
    @Override
    public Collection<IConnectionConstraint> getConnectionConstraints() {
        return connectionConstraints;
    }

    @NotNull
    @Override
    public Collection<IWorkflowConnection> getConnections() {
        return this.connections;
    }

    @Override
    public void addConnection(@NotNull IWorkflowConnection connection) {
        this.connections.add(connection);
    }

    /**
     * check, if data is valid for this connector
     *
     * @param data input data
     * @return true, if data is valid, false otherwise
     */
    private boolean validate(@Nullable IData data) {
        //check runtime constraints
        for (IRuntimeConstraint constraint : this.getRuntimeConstraints()) {
            if (!constraint.compliantWith(data)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @Nullable IData getData() {
        return this.data;
    }

    /**
     * add a data object
     *
     * @param data data to set
     */
    public void setData(@Nullable IData data) {
        if (!validate(data))
            throw new IllegalArgumentException("Provided data is not applicable to this connector");
        this.data = data;
    }

    @NotNull
    @Override
    public IWorkflowNode getEntity() {
        return this.entity;
    }

    @Override
    public void reset() {
        for(IWorkflowConnection connection : this.getConnections()){
            connection.reset();
        }
        this.getConnections().clear();
        this.data = null;
    }

    @Override
    public boolean ready() {
        return this.validate(this.getData());
    }

}
