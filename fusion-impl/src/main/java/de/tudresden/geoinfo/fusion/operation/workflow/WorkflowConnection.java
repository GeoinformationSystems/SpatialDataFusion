package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
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
    public WorkflowConnection(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IInputConnector input, @NotNull IOutputConnector output) {
        super(identifier, title, description);
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
        this(null, null, null, input, output);
    }

    @NotNull
    @Override
    public IInputConnector getInput() {
        return input;
    }

    @NotNull
    @Override
    public IOutputConnector getOutput() {
        return output;
    }

    @Override
    public void updateState() {
        this.setState(ElementState.READY);
        for (IConnectionConstraint connectionConstraint : this.input.getConnectionConstraints()) {
            if (!connectionConstraint.compliantWith(this.output)) {
                this.setState(ElementState.IMPROPER_CONFIGURATION);
            }
        }
        for (IConnectionConstraint connectionConstraint : this.output.getConnectionConstraints()) {
            if (!connectionConstraint.compliantWith(this.input)) {
                this.setState(ElementState.IMPROPER_CONFIGURATION);
            }
        }
    }
}
