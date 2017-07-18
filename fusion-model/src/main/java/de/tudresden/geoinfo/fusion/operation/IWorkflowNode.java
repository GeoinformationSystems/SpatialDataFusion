package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * basic workflow entity (object that provides/generates data)
 */
public interface IWorkflowNode extends IWorkflowElement, IRDFResource {

    /**
     * get description for this connector
     * @return connector description
     */
    String getDescription();

    /**
     * get input connectors
     *
     * @return input connectors of the operation
     */
    @NotNull
    Collection<IInputConnector> getInputConnectors();

    /**
     * get input connector by identifier
     *
     * @param identifier connector identifier
     * @return input connector or null, if no connector with specified identifier exists
     */
    @Nullable
    IInputConnector getInputConnector(@NotNull IIdentifier identifier);

    /**
     * get input connector by local identifier
     *
     * @param localIdentifier local connector identifier
     * @return input connector or null, if no connector with specified local identifier exists
     */
    @Nullable
    IInputConnector getInputConnector(@NotNull String localIdentifier);

    /**
     * get output connectors
     *
     * @return output connectors of the operation
     */
    @NotNull
    Collection<IOutputConnector> getOutputConnectors();

    /**
     * get output connector by local identifier
     *
     * @param identifier connector identifier
     * @return input connector or null, if no connector with specified identifier exists
     */
    @Nullable
    IOutputConnector getOutputConnector(@NotNull IIdentifier identifier);

    /**
     * get output connector by local identifier
     *
     * @param localIdentifier local connector identifier
     * @return output connector or null, if no connector with specified local identifier exists
     */
    @Nullable
    IOutputConnector getOutputConnector(@NotNull String localIdentifier);

    /**
     * get direct ancestors in a workflow
     *
     * @return collection of ancestors
     */
    @NotNull
    Collection<IWorkflowNode> getAncestors();

    /**
     * get direct successors in a workflow
     *
     * @return collection of successors
     */
    @NotNull
    Collection<IWorkflowNode> getSuccessors();

    /**
     * execute workflow node
     */
    void execute();

    /**
     * check if workflow node has been successfully executed
     */
    boolean success();

}
