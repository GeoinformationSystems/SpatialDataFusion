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
 * Input connector implementation
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class InputConnector extends AbstractWorkflowConnector implements IInputConnector {

    private IData defaultData;

    /**
     * constructor
     *@param identifier local element identifier
     * @param entity                associated workflow entity
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @Nullable Collection<IRuntimeConstraint> runtimeConstraints, @Nullable Collection<IConnectionConstraint> connectionConstraints, IData defaultData) {
        super(identifier, description, entity, runtimeConstraints, connectionConstraints);
        this.defaultData = defaultData;
    }

    /**
     * constructor
     * @param identifier element identifier
     * @param entity                associated workflow entity
     * @param runtimeConstraints    connector runtime constraints
     * @param connectionConstraints connection constraints
     */
    public InputConnector(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IWorkflowNode entity, @NotNull IRuntimeConstraint[] runtimeConstraints, @NotNull IConnectionConstraint[] connectionConstraints, @Nullable IData defaultData) {
        this(identifier, description, entity, new HashSet<>(Arrays.asList(runtimeConstraints)), new HashSet<>(Arrays.asList(connectionConstraints)), defaultData);
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

    @Override
    public void connect(@NotNull IOutputConnector connector) {
        IWorkflowConnection connection = new WorkflowConnection(this, connector);
        super.addConnection(connection);
    }

    @Override
    public IData getDefaultData() {
        return defaultData;
    }

    @Override
    public IData getData() {
        return super.getData() != null ? super.getData() : this.getDefaultData();
    }

}
