package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Workflow input connection
 */
public interface IInputConnector extends IWorkflowConnector {

    /**
     * get associated output connectors
     *
     * @return output connectors associated with this connector
     */
    @NotNull
    Collection<IOutputConnector> getOutputConnectors();

    /**
     * add output connectors
     *
     * @param connector output connector
     * @return true, if connector has been set
     */
    boolean addOutputConnector(@NotNull IOutputConnector connector);

    /**
     * get default output connector for this connector
     *
     * @return default output connector , null if no default is specified
     */
    @Nullable
    IOutputConnector getDefault();

}
