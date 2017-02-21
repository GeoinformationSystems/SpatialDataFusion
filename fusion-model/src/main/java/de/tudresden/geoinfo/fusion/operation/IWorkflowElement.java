package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;

/**
 * basic workflow element
 */
public interface IWorkflowElement extends IResource {

    /**
     * get current state of the workflow element
     *
     * @return current state of the workflow element
     */
    @NotNull
    ElementState getState();

}
