package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WMSHandler;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@SessionScoped
public class Basemap implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String,WMSHandler> mapHandler;
    private Set<String> layers;
    private Set<String> selection;

    public Set<String> getLayers() {
        return layers;
    }

    public void setLayers(Set<String> layers) {
        this.layers = layers;
    }

    public void addLayer(String layer) {
        if(this.layers == null)
            this.layers = new HashSet<>();
        this.layers.add(layer);
    }

    public Set<String> getSelection() {
        return selection;
    }
	
	public void setSelection(Set<String> selection) {
	    this.selection = selection;
		update();
    }

    private void addSelection(String layer) {
        if(this.selection == null)
            this.selection = new HashSet<>();
        this.selection.add(layer);
    }

	private void update(){
		//update layer view
		StringBuilder sLayers = new StringBuilder();
		if(this.selection != null && !selection.isEmpty()){
            for(String layer : this.getSelection()){
                sLayers.append(layer + ";");
            }
        }
		//call javascript update
		if(sLayers.length() > 0)
			RequestContext.getCurrentInstance().execute("olMap.updateBasemapSelection('" + sLayers.substring(0, sLayers.length()-1) + "')");
	}

	public void registerWMSHandler(final String key, final String url, final String sLayer, final boolean selected){
        //only add key, if url or sLayer are empty
        if(url == null || url.isEmpty() || sLayer == null || sLayer.isEmpty())
            this.addLayer(key);
	    else {
            try {
                WMSHandler handler = new WMSHandler(url);
                handler.setLayer(sLayer);
                this.addWMSHandler(key, handler);
                this.addLayer(key);
            } catch (Exception e) {
                MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "WMS Handler Error", e.getLocalizedMessage());
            }
        }
        if(selected)
            this.addSelection(key);
    }

    private void addWMSHandler(String key, WMSHandler handler) {
	    if(mapHandler == null)
	        this.mapHandler = new HashMap<>();
        this.mapHandler.put(key, handler);
    }


    private String tmp_basemapKey;
    public String getTmp_basemapKey() {
        return this.tmp_basemapKey;
    }
    public void setTmp_basemapKey(String tmp_basemapKey) {
        this.tmp_basemapKey = tmp_basemapKey;
    }

    private String tmp_basemapUrl;
    public String getTmp_basemapUrl() {
        return this.tmp_basemapUrl;
    }
    public void setTmp_basemapUrl(String tmp_basemapUrl) {
        this.tmp_basemapUrl = tmp_basemapUrl;
    }

    private String tmp_basemapSelected;
    public String getTmp_basemapSelected() {
        return this.tmp_basemapSelected;
    }
    public void setTmp_basemapSelected(String tmp_basemapSelectedLayer) {
        this.tmp_basemapSelected = tmp_basemapSelectedLayer;
    }

    private WMSHandler tmp_basemapHandler;
    public void setTmp_basemapHandler(WMSHandler tmp_basemapHandler) {
        this.tmp_basemapHandler = tmp_basemapHandler;
    }

    public Set<String> getTmp_layers(){
	    return tmp_basemapHandler != null ? tmp_basemapHandler.getSupportedLayers() : Collections.emptySet();
    }

    public void tmp_reset() {
        this.setTmp_basemapKey(null);
        this.setTmp_basemapSelected(null);
        this.setTmp_basemapUrl(null);
        this.setTmp_basemapHandler(null);
    }

    public void initHandler() {
	    //check entries
        if(this.tmp_basemapUrl == null || this.tmp_basemapUrl.isEmpty()) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "No URL", "A WMS endpoint must be provided");
            return;
        }
        //test valid url
        if(!this.tmp_basemapUrl.matches(URILiteral.getURLRegex())) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "No valid URL", "The WMS endpoint is not a valid URL");
            return;
        }
        //try to create WMS handler
        try {
            setTmp_basemapHandler(new WMSHandler(tmp_basemapUrl));
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "Loaded capabilities", "Successfully loaded WMS capabilities");
        } catch (IOException e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "Parser Error", e.getLocalizedMessage());
        }
    }

    public void addHandler() {
        if(this.tmp_basemapKey == null || this.tmp_basemapKey.isEmpty())
            this.tmp_basemapKey = tmp_basemapSelected;
        RequestContext.getCurrentInstance().execute("f_registerWMSBasemapFromJSF(olMap,'" + this.tmp_basemapKey + "','" + this.tmp_basemapUrl + "','" + this.tmp_basemapSelected + "')");
        tmp_basemapHandler.setLayer(tmp_basemapSelected);
        this.addWMSHandler(tmp_basemapKey, tmp_basemapHandler);
        this.addLayer(tmp_basemapKey);
        this.addSelection(tmp_basemapKey);
        this.tmp_reset();
        update();
    }

}
