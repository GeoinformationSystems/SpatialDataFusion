package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WPSHandler;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.operation.WPSProxyOperation;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ManagedBean
@SessionScoped
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, WPSHandler> gpHandler;
    private String selection;
    private String tmp_processKey;
    private String tmp_processUrl;
    private String tmp_processSelected;
    private WPSHandler tmp_processHandler;
    private JSONArray connections;

    @PostConstruct
    public void init() {
        this.gpHandler = new HashMap<>();
    }

    public Set<String> getProcesses() {
        return gpHandler.keySet();
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public WPSHandler getSelectionHandler() {
        return this.gpHandler.get(this.getSelection());
    }

    public void registerWPSHandler(final String key, final String url, final String sProcess) {
        if (url == null || url.isEmpty() || sProcess == null || sProcess.isEmpty())
            return;
        try {
            WPSHandler handler = new WPSHandler(url);
            handler.setProcess(sProcess);
            this.addWPSHandler(key, handler);
        } catch (Exception e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "WPS Handler Error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void addWPSHandler(String key, WPSHandler handler) {
        this.gpHandler.put(key, handler);
    }

    public String getTmp_processKey() {
        return this.tmp_processKey;
    }

    public void setTmp_processKey(String tmp_processKey) {
        this.tmp_processKey = tmp_processKey;
    }

    public String getTmp_processUrl() {
        return this.tmp_processUrl;
    }

    public void setTmp_processUrl(String tmp_processUrl) {
        this.tmp_processUrl = tmp_processUrl;
    }

    public String getTmp_processSelected() {
        return this.tmp_processSelected;
    }

    public void setTmp_processSelected(String tmp_processSelected) {
        this.tmp_processSelected = tmp_processSelected;
    }

    public void setTmp_processHandler(WPSHandler tmp_processHandler) {
        this.tmp_processHandler = tmp_processHandler;
    }

    public Set<String> getTmp_processes() {
        return tmp_processHandler != null ? tmp_processHandler.getSupportedProcesses() : Collections.emptySet();
    }

    public void tmp_reset() {
        this.setTmp_processKey(null);
        this.setTmp_processSelected(null);
        this.setTmp_processUrl(null);
        this.setTmp_processHandler(null);
    }

    public void initHandler() {
        //check entries
        if (this.tmp_processUrl == null || this.tmp_processUrl.isEmpty()) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "No URL", "A WPS endpoint must be provided");
            return;
        }
        //test valid url
        if (!this.tmp_processUrl.matches(URILiteral.getURLRegex())) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "No valid URL", "The WPS endpoint is not a valid URL");
            return;
        }
        //try to create WPS handler
        try {
            setTmp_processHandler(new WPSHandler(tmp_processUrl));
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "Loaded capabilities", "Successfully loaded WPS capabilities");
        } catch (IOException | RuntimeException e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "Parser Error", e.getLocalizedMessage());
        }
    }

    public void addHandler() {
        if (this.tmp_processKey == null || this.tmp_processKey.isEmpty())
            this.tmp_processKey = tmp_processSelected;
        try {
            tmp_processHandler.setProcess(tmp_processSelected);
            this.addWPSHandler(tmp_processKey, tmp_processHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.tmp_reset();
        }
    }

    public void initJSPlumb() {
        WPSHandler handler = this.getSelectionHandler();
        RequestContext.getCurrentInstance().execute("f_initSinglePlumb('" + handler.getJSONProcessDescription().toString() + "')");
    }

    public void clearJSPlumb() {
        RequestContext.getCurrentInstance().execute("f_clearSinglePlumb()");
    }

    public JSONArray getConnections() {
        return connections;
    }

    public void setConnections(final String sConnections) {
        this.connections = new JSONArray(sConnections);
    }

    public void executeProcess() {
        //init WPS proxy instance
        WPSProxyOperation proxy = new WPSProxyOperation(getSelectionHandler().getProcessDescription());
        //init WFS input handler
        System.out.println(connections);
        //check for appropriate output handler
    }

}
