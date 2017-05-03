package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.AbstractOWSHandler;
import de.tudresden.geoinfo.client.handler.WFSHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name="layer")
@SessionScoped
public class Layer extends AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    boolean multiSelect() {
        return true;
    }

    @Override
    WFSHandler initOWSHandler(String uid, String sBaseURL) throws IOException {
        return new WFSHandler(uid, sBaseURL);
    }

    @Override
    public void registerOWSOffering(AbstractOWSHandler handler, String selectedOffering) {
        RequestContext.getCurrentInstance().execute("f_registerWFSLayerFromJSF(" + handler.getIdentifier() + ",olMap,'" + handler.getBase().toString() + "','" + selectedOffering + "')");
        update();
    }

    @Override
    public WFSHandler getHandler(@NotNull String uid) {
        return (WFSHandler) super.getHandler(uid);
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
     * @param selectedFeatures input JSON string (called by remote command pf_setSelectedFeatures)
     */
    public void setSelectedFeatures(final String selectedFeatures) {
        JSONArray jSelectedFeatures = !selectedFeatures.isEmpty() ? new JSONArray(unescapeJSON(selectedFeatures)) : new JSONArray();
        //set selected features
        for(Object layerObject : jSelectedFeatures){
            setSelectedFeatures(((JSONObject) layerObject).getString("layer"), ((JSONObject) layerObject).getJSONArray("features"));
        }
        //initialize output descriptions for selected layers
        JSONArray jSelectedFeaturesByLayer = new JSONArray();
        for(String sLayer : this.getSelectedOfferings()){
            jSelectedFeaturesByLayer.put(this.getHandler(sLayer).getJSONDescription());
        }
        //call f_setInputDescriptions in jsPlumb
        RequestContext.getCurrentInstance().execute("f_setInputDescriptions('" + jSelectedFeaturesByLayer.toString() + "')");
    }

    private String unescapeJSON(String sJSON) {
        return sJSON.replace("\\\"", "\"").replaceAll("^\"+", "").replaceAll("\"+$", "");
    }

    private void setSelectedFeatures(@Nullable String sLayer, @Nullable JSONArray features) {
        if(sLayer == null || sLayer.isEmpty() || features == null)
            return;
        this.getHandler(sLayer).setSelectedFeatures(features);
    }

}
