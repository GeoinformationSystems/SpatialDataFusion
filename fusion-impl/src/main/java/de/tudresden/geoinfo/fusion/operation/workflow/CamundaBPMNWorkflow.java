package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.operation.IWorkflow;

/**
 * Camunda BPNM workflow description
 */
public class CamundaBPMNWorkflow extends Workflow implements IWorkflow {

    CamundaBPMNModel model;

    /**
     * constructor
     *
     * @param model Camunda BPMN model
     */
    public CamundaBPMNWorkflow(CamundaBPMNModel model) {
        super();
        this.model = model;
        if (!initializeNodes())
            throw new RuntimeException("Could not initialize workflow nodes for " + model.getIdentifier());
    }

    private boolean initializeNodes() {
        //TODO parse BPMN
        return false;
    }

}
