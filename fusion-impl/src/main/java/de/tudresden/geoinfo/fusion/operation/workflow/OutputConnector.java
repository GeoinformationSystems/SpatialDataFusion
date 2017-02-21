package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Output connector implementation
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class OutputConnector extends AbstractWorkflowConnector implements IOutputConnector {

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        super(identifier, title, description, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param title                 IO title
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull String title, @NotNull IWorkflowNode entity, @Nullable Set<IRuntimeConstraint> runtimeConstraints, @Nullable Set<IConnectionConstraint> connectionConstraints) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(identifier, title, description, entity, new HashSet<>(Arrays.asList(runtimeConstraints)), new HashSet<>(Arrays.asList(connectionConstraints)));
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param identifier            IO identifier
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull IIdentifier identifier, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(identifier, null, null, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     *
     * @param entity                associated workflow entity
     * @param title                 IO title
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull String title, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints) {
        this(null, title, null, entity, runtimeConstraints, connectionConstraints);
    }

    @Override
    public boolean addInputConnector(@NotNull IInputConnector connector) {
        IWorkflowConnection connection = new WorkflowConnection(connector, this);
        return connection.getState().equals(ElementState.READY);
    }

    @NotNull
    @Override
    public Collection<IInputConnector> getInputConnectors() {
        Collection<IInputConnector> connectors = new HashSet<>();
        for (IWorkflowConnection connection : this.getConnections()) {
            connectors.add(connection.getInput());
        }
        return connectors;
    }
}
