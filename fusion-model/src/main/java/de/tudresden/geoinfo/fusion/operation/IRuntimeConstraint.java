package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import org.jetbrains.annotations.Nullable;

/**
 * runtime constraint
 */
public interface IRuntimeConstraint {

    /**
     * check whether data object complies with constraint
     *
     * @param data data object to be tested
     * @return true, if data satisfies constraint
     */
    boolean compliantWith(@Nullable IData data);

}
