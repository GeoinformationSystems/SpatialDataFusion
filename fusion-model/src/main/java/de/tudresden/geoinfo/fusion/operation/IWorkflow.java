package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Operation workflow
 */
public interface IWorkflow extends IWorkflowNode {

    /**
     * get elements in the workflow
     *
     * @return all elements in the workflow
     */
    @NotNull
    Collection<IWorkflowNode> getWorkflowNodes();

}
