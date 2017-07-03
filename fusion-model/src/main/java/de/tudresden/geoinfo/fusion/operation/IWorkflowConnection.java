package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.NotNull;

/**
 * IO connection in a workflow
 */
public interface IWorkflowConnection extends IWorkflowElement {

    /**
     * get input connector
     *
     * @return input connector
     */
    @NotNull
    IInputConnector getInputConnector();

    /**
     * get output connector
     *
     * @return output connector
     */
    @NotNull
    IOutputConnector getOutputConnector();

}
