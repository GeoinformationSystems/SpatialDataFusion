package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
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
     */
    public AbstractOperation(@Nullable IIdentifier identifier) {
        super(identifier, null, null);
    }

    @NotNull
    @Override
    public abstract String getTitle();

    @Nullable
    @Override
    public abstract String getDescription();

    @NotNull
    @Override
    public Map<IIdentifier, IData> execute(@Nullable Map<IIdentifier, IData> inputs) {
        //set inputs from input map
        if(inputs != null)
            super.connectInputs(inputs);
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
        this.addOutputConnector(null, OUT_START, "Start time of the operation",
                new IRuntimeConstraint[]{
                        new MandatoryDataConstraint(),
                        new BindingConstraint(LongLiteral.class)},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:double"))
                });
        //add process runtime
        this.addOutputConnector(null, OUT_RUNTIME, "Runtime of the operation",
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
                System.currentTimeMillis(),
                new Metadata("Start time", "Start time of the operation in Unix time", TIME_UNIT, LongLiteral.getMaxRange()),
                this));
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
                System.currentTimeMillis() - this.getStartTime().resolve(),
                new Metadata("Runtime", "Runtime of the operation in milliseconds", TIME_UNIT, LongLiteral.getPositiveRange()),
                this));
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

    /**
     * execute the operation
     */
    public abstract void executeOperation();

}
