package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * basic workflow entity (object that provides/generates data)
 */
public interface IWorkflowNode extends IWorkflowElement {

    /**
     * get input connectors
     *
     * @return input connectors of the operation
     */
    @NotNull
    Collection<IInputConnector> getInputConnectors();

    /**
     * get input connector by id
     *
     * @param id connector identifier
     * @return input connector or null, if no such connector exist
     */
    @Nullable
    IInputConnector getInputConnector(@NotNull IIdentifier id);

    /**
     * get input connector by title
     *
     * @param title connector title
     * @return input connector or null, if no such connector exist
     */
    @Nullable
    IInputConnector getInputConnector(@NotNull String title);

    /**
     * get output connectors
     *
     * @return output connectors of the operation
     */
    @NotNull
    Collection<IOutputConnector> getOutputConnectors();

    /**
     * get output connector by id
     *
     * @param id connector identifier
     * @return input connector or null, if no such connector exist
     */
    @Nullable
    IOutputConnector getOutputConnector(@NotNull IIdentifier id);

    /**
     * get output connector by title
     *
     * @param title connector title
     * @return output connector or null, if no such connector exist
     */
    @Nullable
    IOutputConnector getOutputConnector(@NotNull String title);

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
     * execute action associated with element
     */
    void execute();

    /**
     * check if node has been executed successfully (all output connectors must be ready)
     *
     * @return true, if node has been executed successfully
     */
    boolean isSuccess();

}
