package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import de.tudresden.geoinfo.fusion.operation.workflow.AbstractWorkflowNode;

/**
 * data object that can be used as input in a workflow
 */
public class InputData extends AbstractWorkflowNode {

    private static final String OUT_DATA = "OUT_DATA";

    private IData data;

    /**
     * constructor
     *
     * @param data data object
     */
    public InputData(IData data) {
        super(null, OUT_DATA, null, false);
        this.data = data;
        this.initializeConnectors();
        this.getOutputConnector().setData(this.data);
    }

    /**
     * get default output connector
     *
     * @return default output connector
     */
    public IOutputConnector getOutputConnector() {
        return this.getOutputConnector(OUT_DATA);
    }

    @Override
    public void performAction() {
        //do nothing
    }

    @Override
    protected void initializeInputConnectors() {
        //do nothing
    }

    @Override
    protected void initializeOutputConnectors() {
        this.addOutputConnector(OUT_DATA, "Workflow input",
                new IRuntimeConstraint[]{
                        new MandatoryConstraint()},
                null);
    }

}
