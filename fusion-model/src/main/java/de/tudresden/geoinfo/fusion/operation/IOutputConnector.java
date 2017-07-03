package de.tudresden.geoinfo.fusion.operation;

/**
 * Workflow output connector
 */
public interface IOutputConnector extends IWorkflowConnector {

    /**
     * add an input connector to this connector
     *
     * @param inputConnector input connector
     */
    void connect(IInputConnector inputConnector);

}
