package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import de.tudresden.geoinfo.fusion.operation.IWorkflowNode;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class JSONUtils {

    public static @NotNull JSONObject getJSONDescription(@NotNull WFSProxy wfsProxy) {
        if (wfsProxy.getSelectedOffering() == null)
            return new JSONObject();
        //set hidden entries
        Set<String> hiddenInputs = new HashSet<>();
        hiddenInputs.add(wfsProxy.getInputConnector("IN_FORMAT").getIdentifier().getLocalIdentifier());
        hiddenInputs.add(wfsProxy.getInputConnector("IN_LAYER").getIdentifier().getLocalIdentifier());
        hiddenInputs.add(wfsProxy.getInputConnector("IN_FID").getIdentifier().getLocalIdentifier());
        Set<String> hiddenOutputs = new HashSet<>();
        hiddenOutputs.add(wfsProxy.getOutputConnector("OUT_START").getIdentifier().getLocalIdentifier());
        hiddenOutputs.add(wfsProxy.getOutputConnector("OUT_RUNTIME").getIdentifier().getLocalIdentifier());
        //get description
        return JSONUtils.getJSONDescription(wfsProxy, hiddenInputs, hiddenOutputs);
    }

    /**
     * get process io description as json
     *
     * @param sLayer      selected layer
     * @param sIdentifier selected fid
     * @return JSON io process description
     */
    private static @NotNull JSONObject getJSONDescription(@NotNull WFSProxy wfsProxy, @NotNull String sLayer, @NotNull String sIdentifier) {
        return new JSONObject()
                .put("identifier", sIdentifier + "@" + sLayer)
                .put("minOccurs", 1)
                .put("maxOccurs", 1)
                .put("title", sIdentifier)
                .put("defaultFormat", JSONUtils.getJSONDescription(wfsProxy.getCapabilities().getOutputDescription().getDefaultFormat()))
                .put("supportedFormats", JSONUtils.getJSONDescription(wfsProxy.getCapabilities().getOutputDescription().getSupportedFormats()));
    }

    /**
     * get process description as json (used by jsPlumb)
     *
     * @return JSON process description
     */
    public static @NotNull JSONObject getJSONProcessDescription(@NotNull WPSProxy wpsProxy) {
        if (wpsProxy.getProcessDescription() == null)
            return new JSONObject();
        //set hidden entries
        Set<String> hiddenInputs = new HashSet<>();
        hiddenInputs.add(wpsProxy.getInputConnector("IN_VERSION").getIdentifier().getLocalIdentifier());
        Set<String> hiddenOutputs = new HashSet<>();
        hiddenOutputs.add(wpsProxy.getOutputConnector("OUT_START").getIdentifier().getLocalIdentifier());
        hiddenOutputs.add(wpsProxy.getOutputConnector("OUT_RUNTIME").getIdentifier().getLocalIdentifier());
        //get description
        return JSONUtils.getJSONDescription(wpsProxy, hiddenInputs, hiddenOutputs);
    }

    /**
     * get node description as json
     *
     * @param node input node
     * @return JSON node description
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IWorkflowNode node) {
        return new JSONObject()
                .put("identifier", node.getIdentifier().getGlobalIdentifier())
                .put("title", node.getIdentifier().getLocalIdentifier())
                .put("description", node.getDescription())
                .put("inputs", getJSONProcessDescription(node.getInputConnectors()))
                .put("outputs", getJSONProcessDescription(node.getOutputConnectors()))
                .put("type", getNodeType(node));
    }

    /**
     * get node description as json
     *
     * @param node          input node
     * @param hiddenInputs  input connectors to be hidden
     * @param hiddenOutputs output connectors to be hidden
     * @return
     */
    public static @NotNull JSONObject getJSONDescription(@NotNull IWorkflowNode node, Set<String> hiddenInputs, Set<String> hiddenOutputs) {
        return new JSONObject()
                .put("identifier", node.getIdentifier().getLocalIdentifier())
                .put("title", node.getIdentifier().getLocalIdentifier())
                .put("description", node.getDescription())
                .put("inputs", getJSONProcessDescription(node.getInputConnectors(), hiddenInputs))
                .put("outputs", getJSONProcessDescription(node.getOutputConnectors(), hiddenOutputs))
                .put("type", getNodeType(node));
    }

    /**
     * get type of a node
     *
     * @param node input node
     * @return type of input node
     */
    private static String getNodeType(@NotNull IWorkflowNode node) {
        if (node instanceof WFSProxy)
            return "wfs";
        if (node instanceof WPSProxy)
            return "wps";
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
        for (IWorkflowConnector connector : connectors) {
            jsonArray.put(getJSONDescription(connector));
        }
        return jsonArray;
    }

    /**
     * get process connector descriptions as json
     *
     * @param connectors       workflow connectors
     * @param hiddenConnectors connectors to be hidden
     * @return JSON connector descriptions
     */
    private static @NotNull JSONArray getJSONProcessDescription(@NotNull Collection<? extends IWorkflowConnector> connectors, @NotNull Set<String> hiddenConnectors) {
        JSONArray jsonArray = new JSONArray();
        for (IWorkflowConnector connector : connectors) {
            if (!hiddenConnectors.contains(connector.getIdentifier().getLocalIdentifier()))
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
                .put("identifier", connector.getIdentifier().getGlobalIdentifier())
                .put("minOccurs", getMinOccurs(connector))
                .put("maxOccurs", getMaxOccurs(connector))
                .put("title", connector.getIdentifier().getLocalIdentifier())
                .put("description", connector.getDescription())
                .put("defaultFormat", formatConstraint != null ? getJSONDescription(formatConstraint.getDefaultFormat()) : "undefined")
                .put("supportedFormats", formatConstraint != null ? getJSONDescription(formatConstraint.getSupportedFormats()) : "undefined");
    }

    private static int getMinOccurs(@NotNull IWorkflowConnector connector) {
        for (IRuntimeConstraint constraint : connector.getRuntimeConstraints()) {
            if (constraint instanceof MandatoryDataConstraint) {
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
        for (IConnectionConstraint constraint : connector.getConnectionConstraints()) {
            if (constraint instanceof IOFormatConstraint) {
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
