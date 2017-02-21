package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import org.jetbrains.annotations.Nullable;

/**
 * Mandatory constraint
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class MandatoryConstraint implements IRuntimeConstraint {

    @Override
    public boolean compliantWith(@Nullable IData data) {
        return data != null;
    }

}
