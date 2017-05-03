package de.tudresden.geoinfo.fusion.operation;

import org.jetbrains.annotations.Nullable;

/**
 * Basic constraint
 */
public interface IConnectionConstraint {

    /**
     * check whether connector complies with constraint
     *
     * @param connector connector to be tested
     * @return true, if connector satisfies constraint
     */
    boolean compliantWith(@Nullable IWorkflowConnector connector);

}
