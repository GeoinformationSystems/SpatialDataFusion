package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Workflow output connector
 */
public interface IOutputConnector extends IWorkflowConnector {

    /**
     * get associated input connectors
     *
     * @return input connectors associated with this connector
     */
    @NotNull
    Collection<IInputConnector> getInputConnectors();

    /**
     * add input connectors
     *
     * @param connector input connector
     * @return true, if connector has been set
     */
    boolean addInputConnector(@NotNull IInputConnector connector);

}
