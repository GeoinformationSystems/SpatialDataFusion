package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import org.jetbrains.annotations.Nullable;

/**
 * Workflow input connection
 */
public interface IInputConnector extends IWorkflowConnector {

    /**
     * add an output connector to this connector
     *
     * @param outputConnector output connector
     */
    void connect(IOutputConnector outputConnector);

    /**
     * get default output connector for this connector
     *
     * @return default output connector , null if no default is specified
     */
    @Nullable
    IData getDefaultData();

}
