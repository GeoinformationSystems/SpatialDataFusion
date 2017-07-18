package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Output connector implementation
 */
public class OutputConnector extends AbstractWorkflowConnector implements IOutputConnector {

    /**
     * constructor
     * @param identifier element identifier
     * @param entity                associated workflow entity
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints) {
        super(identifier, description, entity, runtimeConstraints, connectionConstraints);
    }

    /**
     * constructor
     * @param identifier element identifier
     * @param entity                associated workflow entity
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public OutputConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable IRuntimeConstraint[] runtimeConstraints, @Nullable IConnectionConstraint[] connectionConstraints) {
        super(identifier, description, entity, runtimeConstraints, connectionConstraints);
    }

    @Override
    public void connect(@NotNull IInputConnector connector) {
        IWorkflowConnection connection = new WorkflowConnection(connector, this);
        super.addConnection(connection);
    }

}
