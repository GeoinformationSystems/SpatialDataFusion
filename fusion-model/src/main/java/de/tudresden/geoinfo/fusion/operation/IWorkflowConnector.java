package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IWorkflowConnector extends IResource, IWorkflowElement {

    /**
     * get associated workflow entity
     *
     * @return associated workflow entity
     */
    @NotNull
    IWorkflowNode getEntity();

    /**
     * get connections associated with this connector
     *
     * @return associated workflow connections
     */
    @NotNull
    Collection<IWorkflowConnection> getConnections();

    /**
     * add a workflow connection
     *
     * @param connection connection to add
     * @return true if connection has been added
     */
    boolean addConnection(@NotNull IWorkflowConnection connection);

    /**
     * get connector runtime constraints
     *
     * @return runtime constraints
     */
    @NotNull
    Collection<IRuntimeConstraint> getRuntimeConstraints();

    /**
     * get connector connection constraints
     *
     * @return connection constraints
     */
    @NotNull
    Collection<IConnectionConstraint> getConnectionConstraints();

    /**
     * get data object associated with this connector
     *
     * @return data object linked to this connector or null, if no such data object exist
     */
    @Nullable
    IData getData();

    /**
     * associate data object with connector
     *
     * @return true if data has been set
     */
    boolean setData(@NotNull IData data);

}