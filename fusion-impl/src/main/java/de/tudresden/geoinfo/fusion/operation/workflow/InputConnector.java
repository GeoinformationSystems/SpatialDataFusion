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
 * Input connector implementation
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class InputConnector extends AbstractWorkflowConnector implements IInputConnector {

    private IOutputConnector defaultConnector;

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints, IOutputConnector defaultConnector) {
        super(identifier, title, description, entity, runtimeConstraints, connectionConstraints);
        this.defaultConnector = defaultConnector;
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints, IOutputConnector defaultConnector) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints, defaultConnector);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param title                 IO title
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull String title, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints, IOutputConnector defaultConnector) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints, defaultConnector);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints, IOutputConnector defaultConnector) {
        this(identifier, title, description, entity, new HashSet<>(Arrays.asList(runtimeConstraints)), new HashSet<>(Arrays.asList(connectionConstraints)), defaultConnector);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints, IOutputConnector defaultConnector) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints, defaultConnector);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param title                 IO title
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull String title, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints, IOutputConnector defaultConnector) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints, defaultConnector);
    }

    /**
     * get connection attached to input connector
     *
     * @return connection attached to input connector
     */
    public IWorkflowConnection getConnection() {
        if (this.getConnections().isEmpty())
            return null;
        return this.getConnections().iterator().next();
    }

    @NotNull
    @Override
    public Collection<IOutputConnector> getOutputConnectors() {
        Collection<IOutputConnector> connectors = new HashSet<>();
        for (IWorkflowConnection connection : this.getConnections()) {
            connectors.add(connection.getOutput());
        }
        return connectors;
    }

    @Override
    public boolean addOutputConnector(@NotNull IOutputConnector connector) {
        IWorkflowConnection connection = new WorkflowConnection(this, connector);
        return connection.getState().equals(ElementState.READY);
    }

    @Override
    public IOutputConnector getDefault() {
        return defaultConnector;
    }

    @Override
    public IData getData() {
        return super.getData() != null ? super.getData() : (getDefault() != null ? getDefault().getData() : null);
    }

}
