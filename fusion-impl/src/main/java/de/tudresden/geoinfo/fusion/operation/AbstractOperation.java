package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.*;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
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

    private static final IIdentifier UOM_TIME = MetadataVocabulary.MILLISECOND.getIdentifier();
    private static final IMeasurementRange RANGE_TIME = LongLiteral.getPositiveRange();

    private static final String OUT_START_CONNECTOR = "OUT_START";
    private static final String OUT_START_TITLE = "Opertion start time";
    private static final String OUT_START_DESCRIPTION = "Start time of the operation";

    private static final String OUT_RUNTIME_CONNECTOR = "OUT_RUNTIME";
    private static final String OUT_RUNTIME_TITLE = "Opertion runtime";
    private static final String OUT_RUNTIME_DESCRIPTION = "Runtime of the operation";

    /**
     * constructor
     *
     */
    public AbstractOperation(@NotNull IIdentifier identifier, @Nullable String description) {
        super(identifier, description);
    }

    /**
     * constructor
     *
     */
    public AbstractOperation(@NotNull String localIdentifier, @Nullable String description) {
        this(new ResourceIdentifier(null, localIdentifier), description);
    }

    @NotNull
    @Override
    public Map<IIdentifier, IData> execute(@Nullable Map<IIdentifier, IData> inputs) {
        //set inputs from input map
        if(inputs != null)
            super.setInputs(inputs);
        //set inputs from connections
        for (IInputConnector input : this.getInputConnectors()) {
            Collection<IWorkflowConnection> connections = input.getConnections();
            if (!connections.isEmpty())
                input.setData(connections.iterator().next().getOutputConnector().getData());
        }
        //execute
        setOutputConnectors();
        //return result
        return this.getOutputs();
    }

    @Override
    public void execute() {
        this.execute(null);
    }

    /**
     * execute process with runtime measurement
     */
    private void setOutputConnectors() {
        //set start time
        this.setStartTime();
        //execute (set output connectors)
        this.executeOperation();
        //set runtime
        this.setRuntime();
    }

    /**
     * initialize IO connectors
     */
    protected void initializeConnectors() {
        super.initializeConnectors();
    }

    /**
     * remove all output connectors
     */
    protected void clearOutputConnectors() {
        super.clearOutputConnectors();
        this.amendOutputConnectors();
    }

    /**
     * amend output connectors with start time and runtime
     */
    private void amendOutputConnectors() {
        //add process start time
        this.addOutputConnector(OUT_START_CONNECTOR, OUT_START_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new MandatoryDataConstraint(),
                        new BindingConstraint(Measurement.class)},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:double"))
                });
        //add process runtime
        this.addOutputConnector(OUT_RUNTIME_CONNECTOR, OUT_RUNTIME_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new MandatoryDataConstraint(),
                        new BindingConstraint(Measurement.class)},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:double"))
                });
    }

    /**
     * set start time for this process
     */
    private void setStartTime() {
        this.setOutput(OUT_START_CONNECTOR, new Measurement<>(
                new LongLiteral(System.currentTimeMillis()),
                this.getStartTimeMetadata()));
    }

    private IMetadata md_startTime;
    private IMetadata getStartTimeMetadata() {
        if(md_startTime == null)
            md_startTime = new Metadata(OUT_START_TITLE, OUT_START_DESCRIPTION, UOM_TIME, RANGE_TIME, this);
        return md_startTime;
    }

    /**
     * get start time of the last process
     *
     * @return latest process start time
     */
    private @Nullable Measurement getStartTime() {
        //noinspection ConstantConditions
        return (Measurement) getOutputConnector(OUT_START_CONNECTOR).getData();
    }

    /**
     * set runtime of the process
     */
    private void setRuntime() {
        //noinspection ConstantConditions
        long runtime = System.currentTimeMillis() - (Long) this.getStartTime().resolve();
        this.setOutput(OUT_RUNTIME_CONNECTOR, new Measurement<>(
                new LongLiteral(runtime),
                getRuntimeMetadata()));
    }

    private IMetadata md_runtime;
    private IMetadata getRuntimeMetadata() {
        if(md_runtime == null)
            md_runtime = new Metadata(OUT_RUNTIME_TITLE, OUT_RUNTIME_DESCRIPTION, UOM_TIME, RANGE_TIME, this);
        return md_runtime;
    }

    /**
     * get start time of the last process
     *
     * @return latest process start time
     */
    public @Nullable Measurement getRuntime() {
        //noinspection ConstantConditions
        return (Measurement) getOutputConnector(OUT_RUNTIME_CONNECTOR).getData();
    }

    /**
     * get operation outputs
     *
     * @return operation outputs
     */
    private @NotNull Map<IIdentifier, IData> getOutputs() {
        Map<IIdentifier, IData> outputs = new HashMap<>();
        for (IOutputConnector outputConnector : this.getOutputConnectors()) {
            outputs.put(outputConnector.getIdentifier(), outputConnector.getData());
        }
        return outputs;
    }

    /**
     * execute the operation
     */
    public abstract void executeOperation();

}
