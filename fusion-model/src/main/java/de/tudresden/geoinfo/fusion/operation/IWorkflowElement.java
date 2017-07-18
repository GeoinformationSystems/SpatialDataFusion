package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * basic workflow element
 */
public interface IWorkflowElement {

    /**
     * get identifier for this data object
     * @return data identifier
     */
    @NotNull IIdentifier getIdentifier();

    /**
     * check if workflow node is ready to be used within a workflow
     *
     * @return true, if ready
     */
    boolean ready();

    /**
     * reset workflow element to original state
     */
    void reset();

}
