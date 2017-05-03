package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import org.jetbrains.annotations.Nullable;

/**
 * Mandatory constraint
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class MandatoryConnectionConstraint implements IConnectionConstraint {

    @Override
    public boolean compliantWith(@Nullable IWorkflowConnector connector) {
        return connector != null;
    }
}
