package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class JSONUtils {

    /**
     * get node description as json
     * @param node input node
     * @return JSON node description
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IWorkflowNode node) {
        return new JSONObject()
                .put("identifier", node.getIdentifier())
                .put("title", node.getTitle())
                .put("description", node.getDescription())
                .put("inputs", getJSONProcessDescription(node.getInputConnectors()))
                .put("outputs", getJSONProcessDescription(node.getOutputConnectors()))
                .put("type", getNodeType(node));
    }

    /**
     * get node description as json
     * @param node input node
     * @param hiddenInputs input connectors to be hidden
     * @param hiddenOutputs output connectors to be hidden
     * @return
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IWorkflowNode node, Set<IIdentifier> hiddenInputs, Set<IIdentifier> hiddenOutputs) {
        return new JSONObject()
                .put("identifier", node.getIdentifier())
                .put("title", node.getTitle())
                .put("description", node.getDescription())
                .put("inputs", getJSONProcessDescription(node.getInputConnectors(), hiddenInputs))
                .put("outputs", getJSONProcessDescription(node.getOutputConnectors(), hiddenOutputs))
                .put("type", getNodeType(node));
    }

    /**
     * get type of a node
     * @param node input node
     * @return type of input node
     */
    private static String getNodeType(@NotNull IWorkflowNode node) {
        if(node instanceof WFSProxy)
            return "wfs";
        if(node instanceof WPSProxy)
            return "wps";
        if(node instanceof LiteralInputData)
            return "literal";
        else
            return "unknown";
    }

    /**
     * get process connector descriptions as json
     *
     * @param connectors workflow connectors
     * @return JSON connector descriptions
     */
    private static @NotNull JSONArray getJSONProcessDescription(@NotNull Collection<? extends IWorkflowConnector> connectors) {
        JSONArray jsonArray = new JSONArray();
        for(IWorkflowConnector connector : connectors){
            jsonArray.put(getJSONDescription(connector));
        }
        return jsonArray;
    }

    /**
     * get process connector descriptions as json
     *
     * @param connectors workflow connectors
     * @param hiddenConnectors connectors to be hidden
     * @return JSON connector descriptions
     */
    private static @NotNull JSONArray getJSONProcessDescription(@NotNull Collection<? extends IWorkflowConnector> connectors, @NotNull Set<IIdentifier> hiddenConnectors) {
        JSONArray jsonArray = new JSONArray();
        for(IWorkflowConnector connector : connectors){
            if(!hiddenConnectors.contains(connector.getIdentifier()))
                jsonArray.put(getJSONDescription(connector));
        }
        return jsonArray;
    }

    /**
     * get process connector description as json
     *
     * @param connector workflow connector
     * @return JSON connector description
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IWorkflowConnector connector) {
        IOFormatConstraint formatConstraint = getIOFormatConstraint(connector);
        return new JSONObject()
                .put("identifier", connector.getIdentifier())
                .put("minOccurs", getMinOccurs(connector))
                .put("maxOccurs", getMaxOccurs(connector))
                .put("title", connector.getTitle())
                .put("description", connector.getDescription())
                .put("defaultFormat", formatConstraint != null ? getJSONDescription(formatConstraint.getDefaultFormat()) : "undefined")
                .put("supportedFormats", formatConstraint != null ? getJSONDescription(formatConstraint.getSupportedFormats()) : "undefined");
    }

    private static int getMinOccurs(@NotNull IWorkflowConnector connector) {
        for(IRuntimeConstraint constraint : connector.getRuntimeConstraints()){
            if(constraint instanceof MandatoryDataConstraint){
                return 1;
            }
        }
        return 0;
    }

    private static int getMaxOccurs(@NotNull IWorkflowConnector connector) {
        //TODO implement corresponding constraint
        return 1;
    }

    private static @Nullable IOFormatConstraint getIOFormatConstraint(@NotNull IWorkflowConnector connector) {
        for(IConnectionConstraint constraint : connector.getConnectionConstraints()){
            if(constraint instanceof IOFormatConstraint){
                return (IOFormatConstraint) constraint;
            }
        }
        return null;
    }

    /**
     * get io formats description as json
     *
     * @param ioFormats io formats description
     * @return JSON io formats description
     */
    public static @NotNull JSONArray getJSONDescription(@NotNull Set<IOFormat> ioFormats) {
        JSONArray jsonArray = new JSONArray();
        for (IOFormat io : ioFormats) {
            jsonArray = jsonArray.put(getJSONDescription(io));
        }
        return jsonArray;
    }

    /**
     * get io format description as json
     *
     * @param ioFormat io format description
     * @return JSON io format description
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IOFormat ioFormat) {
        return new JSONObject()
                .put("mimetype", ioFormat.getMimetype())
                .put("schema", ioFormat.getSchema())
                .put("type", ioFormat.getType());
    }

}
