package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.JSONUtils;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.ows.OWSServiceOperation;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@ManagedBean(name = "layer")
@SessionScoped
public class WFSBean extends AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<WFSProxy, JSONArray> selectedFeatures = new HashMap<>();

    @Override
    boolean multiSelect() {
        return true;
    }

    @Override
    WFSProxy initOWSHandler(String uid, String sBaseURL) throws IOException {
        return new WFSProxy(new Identifier(uid), new URLLiteral(sBaseURL));
    }

    @Override
    public void registerOWSOffering(OWSServiceOperation handler, String selectedOffering) {
        RequestContext.getCurrentInstance().execute("f_registerWFSLayerFromJSF(" + handler.getIdentifier() + ",olMap,'" + handler.getBase().toString() + "','" + selectedOffering + "')");
        update();
    }

    @Override
    public @Nullable WFSProxy getHandler(@NotNull String uid) {
        return super.getHandler(uid) != null ? (WFSProxy) super.getHandler(uid) : null;
    }

    @Override
    void update() {
        //update layer view
        StringBuilder sLayers = new StringBuilder();
        if (this.getSelectedOfferings() != null && !this.getSelectedOfferings().isEmpty()) {
            for (String layer : this.getSelectedOfferings()) {
                sLayers.append(layer).append(";");
            }
        }
        //call javascript update
        RequestContext.getCurrentInstance().execute("olMap.updateLayerSelection('" + (sLayers.length() > 0 ? sLayers.substring(0, sLayers.length() - 1) : "") + "')");
    }

    /**
     * set currently selected features
     *
     * @param selectedFeatures input JSON string (called by remote command pf_setSelectedFeatures)
     */
    public void setSelectedFeatures(final String selectedFeatures) {
        JSONArray jSelectedFeatures = !selectedFeatures.isEmpty() ? new JSONArray(unescapeJSON(selectedFeatures)) : new JSONArray();
        //set selected features
        for (Object layerObject : jSelectedFeatures) {
            setSelectedFeatures(((JSONObject) layerObject).getString("layer"), ((JSONObject) layerObject).getJSONArray("features"));
        }
        //initialize output descriptions for selected layers
        JSONArray jSelectedFeaturesByLayer = new JSONArray();
        for (String sLayer : this.getSelectedOfferings()) {
            jSelectedFeaturesByLayer.put(JSONUtils.getJSONDescription(this.getHandler(sLayer)));
        }
        //call f_setInputDescriptions in jsPlumb
        RequestContext.getCurrentInstance().execute("f_setInputDescriptions('" + jSelectedFeaturesByLayer.toString() + "')");
    }

    private String unescapeJSON(String sJSON) {
        return sJSON.replace("\\\"", "\"").replaceAll("^\"+", "").replaceAll("\"+$", "");
    }

    private void setSelectedFeatures(@NotNull String sLayer, @Nullable JSONArray features) {
        WFSProxy proxy = this.getHandler(sLayer);
        if(proxy == null)
            return;
        this.setSelectedFeatures(proxy, features);
    }

    /**
     * set feature selection
     *
     * @param selectedFeatures selected features
     */
    private void setSelectedFeatures(@NotNull WFSProxy wfsProxy, @Nullable JSONArray selectedFeatures) {
        wfsProxy.setFeatureIdentifier(this.getSelectedIdentifier(selectedFeatures));
        this.selectedFeatures.put(wfsProxy, selectedFeatures);
    }

    /**
     * get feature selection
     *
     * @return selected features
     */
    public @NotNull JSONArray getSelectedFeatures(@NotNull WFSProxy wfsProxy) {
        return this.selectedFeatures.containsKey(wfsProxy) ? this.selectedFeatures.get(wfsProxy) : new JSONArray();
    }

    /**
     * get selected feature identifier
     *
     * @return selected feature identifier
     */
    @Nullable
    private Set<String> getSelectedIdentifier(@Nullable JSONArray selectedFeatures) {
        if(selectedFeatures == null)
            return null;
        Set<String> fids = new HashSet<>();
        for (Object featureObject : selectedFeatures) {
            fids.add(((JSONObject) featureObject).getString("id"));
        }
        return fids;
    }

}
