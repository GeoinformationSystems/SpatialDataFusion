package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Basic operation object
 */
public interface IOperation extends IWorkflowNode {

    /**
     * executes an operation
     *
     * @param input input parameters used to execute the operation
     * @return operation output
     */
    @NotNull
    Map<IIdentifier, IData> execute(@Nullable Map<IIdentifier, IData> input);

}
