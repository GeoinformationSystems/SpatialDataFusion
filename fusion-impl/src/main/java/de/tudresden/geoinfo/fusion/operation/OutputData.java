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
public class OutputData<T extends IData> extends AbstractWorkflowNode {

    private Map<String, T> data;
    private final static String DEFAULT_CONNECTOR_TITLE = "Data Output";
    private final static String DEFAULT_NODE_TITLE = "Data Output Node";

    /**
     * constructor
     *
     * @param data data object map (title --> data object)
     */
    public OutputData(@Nullable IIdentifier identifier, @NotNull Map<String, T> data, @NotNull String nodeTitle) {
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
    public OutputData(@Nullable IIdentifier identifier, @Nullable T data, @NotNull String connectorTitle, @NotNull String nodeTitle) {
        this(identifier, Collections.singletonMap(connectorTitle, data), nodeTitle);
    }

    /**
     * constructor
     *
     * @param data data object
     */
    public OutputData(@NotNull T data) {
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

    /**
     * get data object
     * @param key data key
     * @return data
     */
    protected void setData(@NotNull String key, @NotNull T data) {
        this.data.put(key, data);
    }

    @Override
    public void performAction() {
        //do nothing
    }

    @Override
    protected void initializeInputConnectors() {
        for(Map.Entry<String,T> output : this.data.entrySet()) {
            this.addInputConnector(output.getKey(), null,
                    new IRuntimeConstraint[]{
                            new MandatoryDataConstraint()},
                    null,
                    null);
        }
    }

    @Override
    protected void initializeOutputConnectors() {
        //do nothing
    }

    /**
     * get default output connector (assumes that instance was created by constructor(value))
     * @return default output connector or null, if default was not set
     */
    public @Nullable IOutputConnector getOutputConnector() {
        return this.getOutputConnector(DEFAULT_CONNECTOR_TITLE);
    }
}
