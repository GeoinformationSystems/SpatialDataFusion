package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.workflow.AbstractWorkflowNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * data object that can be used as input in a workflow
 */
public class InputData<T extends IData> extends AbstractWorkflowNode {

    private Map<String, T> data;
    private final static String DEFAULT_CONNECTOR_TITLE = "Data Input";
    private final static String DEFAULT_NODE_TITLE = "Data Input Node";

    /**
     * constructor
     *
     * @param data data object map (title --> data object)
     */
    public InputData(@Nullable IIdentifier identifier, @NotNull Map<String, T> data, @NotNull String nodeTitle) {
        super(identifier, nodeTitle, null, false);
        this.data = data;
        this.initializeConnectors();
    }

    /**
     * constructor
     *
     * @param data data object
     * @param connectorTitle title of data object
     * @param nodeTitle title of node
     */
    public InputData(@Nullable IIdentifier identifier, @NotNull T data, @NotNull String connectorTitle, @NotNull String nodeTitle) {
        this(identifier, Collections.singletonMap(connectorTitle, data), nodeTitle);
    }

    /**
     * constructor
     *
     * @param data data object
     */
    public InputData(@NotNull T data) {
        this(null, data, DEFAULT_CONNECTOR_TITLE, DEFAULT_NODE_TITLE);
    }

    /**
     * get data object
     * @param key data key
     * @return data
     */
    protected @Nullable T getData(@NotNull String key) {
        return this.data.get(key);
    }

    @Override
    public void performAction() {
        //do nothing
    }

    @Override
    protected void initializeInputConnectors() {
        //do nothing
    }

    @Override
    protected void initializeOutputConnectors() {
        for(Map.Entry<String,T> output : this.data.entrySet()) {
            this.addOutputConnector(output.getKey(), null,
                    new IRuntimeConstraint[]{
                            new MandatoryDataConstraint()},
                    null);
            this.getOutputConnector(output.getKey()).setData(output.getValue());
        }
    }

    /**
     * get default output connector (assumes that instance was created by constructor(value))
     * @return default output connector or null, if default was not set
     */
    public @Nullable IOutputConnector getOutputConnector() {
        return this.getOutputConnector(DEFAULT_CONNECTOR_TITLE);
    }
}
