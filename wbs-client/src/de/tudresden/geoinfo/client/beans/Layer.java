package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WFSHandler;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@SessionScoped
public class Layer implements Serializable {

    private Map<String, WFSHandler> wfsHandler;
    private Set<String> selection;
    private String tmp_layerKey;
    private String tmp_layerUrl;
    private String tmp_layerSelected;
    private WFSHandler tmp_layerHandler;
    private JSONObject selectedFeatures;

    @PostConstruct
    public void init() {
        this.wfsHandler = new HashMap<>();
    }

    public Set<String> getLayers() {
        return wfsHandler.keySet();
    }

    public Set<String> getSelection() {
        return selection;
    }

    public void setSelection(Set<String> selection) {
        this.selection = selection;
        update();
    }

    private void addSelection(String layer) {
        if (this.selection == null)
            this.selection = new HashSet<>();
        this.selection.add(layer);
    }

    private void update() {
        //update layer view
        StringBuilder sLayers = new StringBuilder();
        if (this.selection != null && !selection.isEmpty()) {
            for (String layer : this.getSelection()) {
                sLayers.append(layer + ";");
            }
        }
        //call javascript update
        RequestContext.getCurrentInstance().execute("olMap.updateLayerSelection('" + (sLayers.length() > 0 ? sLayers.substring(0, sLayers.length() - 1) : "") + "')");
    }

    public void registerWFSHandler(final String key, final String url, final String sLayer, final boolean selected) {
        if (url == null || url.isEmpty() || sLayer == null || sLayer.isEmpty())
            return;
        try {
            WFSHandler handler = new WFSHandler(url);
            handler.setLayer(sLayer);
            this.addWFSHandler(key, handler);
        } catch (Exception e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "WFS Handler Error", e.getLocalizedMessage());
        }
        if (selected)
            this.addSelection(key);
    }

    private void addWFSHandler(String key, WFSHandler handler) {
        this.wfsHandler.put(key, handler);
    }

    public String getTmp_layerKey() {
        return this.tmp_layerKey;
    }

    public void setTmp_layerKey(String tmp_layerKey) {
        this.tmp_layerKey = tmp_layerKey;
    }

    public String getTmp_layerUrl() {
        return this.tmp_layerUrl;
    }

    public void setTmp_layerUrl(String tmp_layerUrl) {
        this.tmp_layerUrl = tmp_layerUrl;
    }

    public String getTmp_layerSelected() {
        return this.tmp_layerSelected;
    }

    public void setTmp_layerSelected(String tmp_layerSelected) {
        this.tmp_layerSelected = tmp_layerSelected;
    }

    public void setTmp_layerHandler(WFSHandler tmp_layerHandler) {
        this.tmp_layerHandler = tmp_layerHandler;
    }

    public Set<String> getTmp_layers() {
        return tmp_layerHandler != null ? tmp_layerHandler.getSupportedLayers() : Collections.emptySet();
    }

    public void tmp_reset() {
        this.setTmp_layerKey(null);
        this.setTmp_layerSelected(null);
        this.setTmp_layerUrl(null);
        this.setTmp_layerHandler(null);
    }

    public void initHandler() {
        //check entries
        if (this.tmp_layerUrl == null || this.tmp_layerUrl.isEmpty()) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "No URL", "A WFS endpoint must be provided");
            return;
        }
        //test valid url
        if (!this.tmp_layerUrl.matches(URILiteral.getURLRegex())) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "No valid URL", "The WFS endpoint is not a valid URL");
            return;
        }
        //try to create WFS handler
        try {
            setTmp_layerHandler(new WFSHandler(tmp_layerUrl));
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "Loaded capabilities", "Successfully loaded WFS capabilities");
        } catch (IOException e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "Parser Error", e.getLocalizedMessage());
        }
    }

    public void addHandler() {
        if (this.tmp_layerKey == null || this.tmp_layerKey.isEmpty())
            this.tmp_layerKey = tmp_layerSelected;
        RequestContext.getCurrentInstance().execute("f_registerWFSLayerFromJSF(olMap,'" + this.tmp_layerKey + "','" + this.tmp_layerUrl + "','" + this.tmp_layerSelected + "')");
        tmp_layerHandler.setLayer(tmp_layerSelected);
        this.addWFSHandler(tmp_layerKey, tmp_layerHandler);
        this.addSelection(tmp_layerKey);
        this.tmp_reset();
        update();
    }

    public JSONObject getSelectedFeatures() {
        return this.selectedFeatures;
    }

    public void setSelectedFeatures(final String selectedFeatures) {
        try {
            this.selectedFeatures = !selectedFeatures.isEmpty() ? new JSONObject(unescapeJSON(selectedFeatures)) : null;
        } catch (JSONException e) {
            this.selectedFeatures = null;
        }
    }

    private String unescapeJSON(String sJSON) {
        return sJSON.replace("\\\"", "\"").replaceAll("^\"+", "").replaceAll("\"+$", "");
    }

}
