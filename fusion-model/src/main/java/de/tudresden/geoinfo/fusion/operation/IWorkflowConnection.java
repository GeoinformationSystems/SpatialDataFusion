package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.NotNull;

/**
 * IO connection in a workflow
 */
public interface IWorkflowConnection extends IWorkflowElement {

    /**
     * get input connection
     *
     * @return input connection
     */
    @NotNull
    IInputConnector getInput();

    /**
     * get output connection
     *
     * @return output connection
     */
    @NotNull
    IOutputConnector getOutput();

}
