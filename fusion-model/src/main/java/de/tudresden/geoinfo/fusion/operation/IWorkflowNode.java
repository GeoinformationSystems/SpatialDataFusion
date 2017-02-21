package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

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
     * get set of input connector identifiers
     *
     * @return input connector identifiers
     */
    @NotNull
    Set<IIdentifier> getInputIdentifiers();

    /**
     * get input connector by id
     *
     * @param id connector identifier
     * @return input connector or null, if no such connector exist
     */
    @Nullable
    IInputConnector getInputConnector(@NotNull IIdentifier id);

    /**
     * get output connectors
     *
     * @return output connectors of the operation
     */
    @NotNull
    Collection<IOutputConnector> getOutputConnectors();

    /**
     * get set of output connector identifiers
     *
     * @return output connector identifiers
     */
    @NotNull
    Set<IIdentifier> getOutputIdentifiers();

    /**
     * get output connector by id
     *
     * @param id connector identifier
     * @return input connector or null, if no such connector exist
     */
    @Nullable
    IOutputConnector getOutputConnector(@NotNull IIdentifier id);

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
     * performs entity action
     */
    void performAction();

}
