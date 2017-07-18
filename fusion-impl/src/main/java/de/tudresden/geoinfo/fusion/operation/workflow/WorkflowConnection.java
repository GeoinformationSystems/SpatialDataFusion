package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Workflow connection implementation
 */
public class WorkflowConnection extends AbstractWorkflowElement implements IWorkflowConnection {

    private IInputConnector input;
    private IOutputConnector output;

    /**
     * constructor
     *
     * @param identifier connection identifier
     * @param input      connection input
     * @param output     connection output
     */
    public WorkflowConnection(@NotNull IIdentifier identifier, @Nullable String description, @NotNull IInputConnector input, @NotNull IOutputConnector output) {
        super(identifier, description);
        this.input = input;
        this.output = output;
        this.input.addConnection(this);
        this.output.addConnection(this);
    }

    /**
     * constructor
     *
     * @param input  input connector
     * @param output output connector
     */
    public WorkflowConnection(@NotNull IInputConnector input, @NotNull IOutputConnector output) {
        this(new ResourceIdentifier(null, output.getIdentifier().getLocalIdentifier() + "_" + input.getIdentifier().getLocalIdentifier()), null, input, output);
    }

    @NotNull
    @Override
    public IInputConnector getInputConnector() {
        return input;
    }

    @NotNull
    @Override
    public IOutputConnector getOutputConnector() {
        return output;
    }

    @Override
    public boolean ready() {
        for (IConnectionConstraint connectionConstraint : this.input.getConnectionConstraints()) {
            if (!connectionConstraint.compliantWith(this.output)) {
                return false;
            }
        }
        for (IConnectionConstraint connectionConstraint : this.output.getConnectionConstraints()) {
            if (!connectionConstraint.compliantWith(this.input)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        // do nothing
    }

}
