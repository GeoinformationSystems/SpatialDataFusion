package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.workflow.AbstractWorkflowNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract operation implementation
 */
public abstract class AbstractOperation extends AbstractWorkflowNode implements IOperation, IWorkflowNode {

    private static final String OUT_START = "OUT_START";
    private static final String OUT_RUNTIME = "OUT_RUNTIME";

    private static IResource TIME_UNIT = Units.MILLISECOND.getResource();

    /**
     * constructor
     *
     * @param identifier  operation identifier
     * @param title       operation title
     * @param description operation description
     * @param initialize  flag: initialize inputs and outputs (if false, initialization must explicitly be invoked by implementing class)
     */
    protected AbstractOperation(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, boolean initialize) {
        super(identifier, title, description, initialize);
    }

    /**
     * constructor
     *
     * @param title       operation title
     * @param description operation description
     * @param initialize  flag: initialize inputs and outputs (if false, initialization must explicitly be invoked by implementing class)
     */
    protected AbstractOperation(@Nullable String title, @Nullable String description, boolean initialize) {
        this(null, title, description, initialize);
    }

    /**
     * constructor
     *
     * @param identifier  operation identifier
     * @param title       operation title
     * @param description operation description
     */
    protected AbstractOperation(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description) {
        super(identifier, title, description, true);
    }

    /**
     * constructor
     *
     * @param title       operation title
     * @param description operation description
     */
    protected AbstractOperation(@Nullable String title, @Nullable String description) {
        this(null, title, description, true);
    }

    /**
     * constructor
     *
     * @param identifier operation identifier
     */
    public AbstractOperation(@NotNull IIdentifier identifier, boolean initialize) {
        this(identifier, null, null, initialize);
    }

    /**
     * constructor
     *
     * @param title operation title
     */
    public AbstractOperation(@NotNull String title, boolean initialize) {
        this(null, title, null, initialize);
    }

    @NotNull
    @Override
    public Map<IIdentifier, IData> execute(@NotNull Map<IIdentifier, IData> inputs) {
        //set inputs
        this.connectInputs(inputs);
        //execute
        performExecute();
        //return result
        return this.getOutputs();
    }

    /**
     * execute process with runtime measurement
     */
    private void performExecute() {
        //set start time
        this.setStartTime();
        //execute (set output connectors)
        this.execute();
        //set runtime
        this.setRuntime();
        updateState();
    }

    /**
     * initialize IO connectors
     */
    protected void initializeConnectors() {
        super.initializeConnectors();
        this.amendOutputConnectors();
    }

    /**
     * amend output connectors with start time and runtime
     */
    private void amendOutputConnectors() {
        //add process start time
        this.addOutputConnector(OUT_START, "Start time of the operation",
                new IRuntimeConstraint[]{
                        new MandatoryDataConstraint(),
                        new BindingConstraint(LongLiteral.class)},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:double"))
                });
        //add process runtime
        this.addOutputConnector(OUT_RUNTIME, "Runtime of the operation",
                new IRuntimeConstraint[]{
                        new MandatoryDataConstraint(),
                        new BindingConstraint(LongLiteral.class)},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:double"))
                });
    }

    /**
     * set start time for this process
     */
    private void setStartTime() {
        this.connectOutput(OUT_START, new LongLiteral(
                null,
                "Start time",
                "Start time of the operation in Unix time",
                System.currentTimeMillis(),
                this,
                LongLiteral.getMaxRange(),
                TIME_UNIT));
    }

    /**
     * get start time of the last process
     *
     * @return latest process start time
     */
    private LongLiteral getStartTime() {
        return (LongLiteral) getOutputConnector(OUT_START).getData();
    }

    /**
     * set runtime of the process
     */
    private void setRuntime() {
        this.connectOutput(OUT_RUNTIME, new LongLiteral(
                null,
                "Runtime",
                "Runtime of the operation in milliseconds",
                System.currentTimeMillis() - this.getStartTime().resolve(),
                this,
                LongLiteral.getPositiveRange(),
                TIME_UNIT));
    }

    /**
     * get start time of the last process
     *
     * @return latest process start time
     */
    public LongLiteral getRuntime() {
        return (LongLiteral) getOutputConnector(OUT_RUNTIME).getData();
    }

    /**
     * get operation outputs
     *
     * @return operation outputs
     */
    private Map<IIdentifier, IData> getOutputs() {
        Map<IIdentifier, IData> outputs = new HashMap<>();
        for (IOutputConnector outputConnector : this.getOutputConnectors()) {
            outputs.put(outputConnector.getIdentifier(), outputConnector.getData());
        }
        return outputs;
    }

    @Override
    public void performAction() {
        //set data for input nodes
        for (IInputConnector input : this.getInputConnectors()) {
            Collection<IWorkflowConnection> connections = input.getConnections();
            if (!connections.isEmpty())
                input.setData(connections.iterator().next().getOutput().getData());
        }
        //execute
        performExecute();
    }

    /**
     * execution of the process (must set output connectors using addOutputConnector(...))
     */
    public abstract void execute();

}
