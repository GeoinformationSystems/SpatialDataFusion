package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * IO connector implementation
 */
public abstract class AbstractWorkflowConnector extends AbstractWorkflowElement implements IWorkflowConnector {

    private IWorkflowNode entity;
    private Collection<IWorkflowConnection> connections;
    private Set<IRuntimeConstraint> runtimeConstraints;
    private Set<IConnectionConstraint> connectionConstraints;
    private IData data;

    /**
     * constructor
     *
     * @param entity                associated entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        super(identifier, title, description);
        this.entity = entity;
        this.connections = new HashSet<>();
        this.runtimeConstraints = runtimeConstraints != null ? runtimeConstraints : new HashSet<>();
        this.connectionConstraints = connectionConstraints != null ? connectionConstraints : new HashSet<>();
    }

    /**
     * constructor
     *
     * @param entity                associated operation
     * @param identifier            IO identifier
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated operation
     * @param title                 IO title
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull String title, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(identifier, title, description, entity, new HashSet<>(Arrays.asList(runtimeConstraints)), new HashSet<>(Arrays.asList(connectionConstraints)));
    }

    /**
     * constructor
     *
     * @param entity                associated operation
     * @param identifier            IO identifier
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated operation
     * @param title                 IO title
     * @param runtimeConstraints    IO connector data constraints
     * @param connectionConstraints IO connector data description constraints
     */
    public AbstractWorkflowConnector(@NotNull String title, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints);
    }

    @NotNull
    @Override
    public Set<IRuntimeConstraint> getRuntimeConstraints() {
        return runtimeConstraints;
    }

    @NotNull
    @Override
    public Set<IConnectionConstraint> getConnectionConstraints() {
        return connectionConstraints;
    }

    @NotNull
    @Override
    public Collection<IWorkflowConnection> getConnections() {
        return this.connections;
    }

    @Override
    public boolean addConnection(@NotNull IWorkflowConnection connection) {
        return connection.getState().equals(ElementState.READY) && this.connections.add(connection);
    }

    /**
     * add a data object
     *
     * @param data data to set
     */
    public boolean setData(@NotNull IData data) {
        if (!validate(data))
            return false;
        this.data = data;
        return true;
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
    public void updateState() {
        if (this.connections != null && !this.connections.isEmpty())
            this.setState(ElementState.READY);
        if (validate(this.data))
            this.setState(ElementState.SUCCESS);
    }

    @Override
    public @Nullable IData getData() {
        return this.data;
    }


    @NotNull
    @Override
    public IWorkflowNode getEntity() {
        return this.entity;
    }

}
