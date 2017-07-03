package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.Nullable;

/**
 * basic workflow element
 */
public interface IWorkflowElement extends IResource {

    /**
     * get title of this element (must be locally unique)
     *
     * @return element title
     */
    @Nullable
    String getTitle();

    /**
     * get description of this element
     *
     * @return element description
     */
    @Nullable
    String getDescription();

    /**
     * check if element is properly configured and ready to be used in a workflow
     *
     * @return true, if node is ready to be used
     */
    boolean isReady();

    /**
     * reset workflow element to original state
     */
    void reset();

}
