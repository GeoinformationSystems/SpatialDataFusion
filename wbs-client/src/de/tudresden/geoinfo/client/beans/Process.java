package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.AbstractOWSHandler;
import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WPSHandler;
import de.tudresden.geoinfo.client.handler.WorkflowHandler;
import de.tudresden.geoinfo.fusion.operation.IWorkflowNode;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name="process")
@SessionScoped
public class Process extends AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private WorkflowHandler workflowHandler;

    @Override
    boolean multiSelect() {
        return false;
    }

    @Override
    WPSHandler initOWSHandler(String uid, String sBaseURL) throws IOException {
        return new WPSHandler(uid, sBaseURL);
    }

    @Override
    public void registerOWSOffering(AbstractOWSHandler handler, String selectedOffering) {
        //do nothing
    }

    @Override
    void update() {
        //do nothing
    }

    @Override
    public WPSHandler getHandler(@NotNull String uid) {
        return (WPSHandler) super.getHandler(uid);
    }

    public @Nullable WPSHandler getHandler() {
        if(this.getSingleSelectedOffering() == null)
            return null;
        return this.getHandler(this.getSingleSelectedOffering());
    }

    public void initJSPlumb() {
        WPSHandler handler = this.getHandler();
        RequestContext.getCurrentInstance().execute("f_initSinglePlumb('" + handler.getJSONProcessDescription().toString() + "')");
    }

    public void clearJSPlumb() {
        RequestContext.getCurrentInstance().execute("f_clearSinglePlumb()");
    }

    public void setWorkflow(final String jsonWorkflowDescription) {
        if(this.workflowHandler == null)
            this.workflowHandler = new WorkflowHandler();
        this.workflowHandler.setWorkflowDescription(initWorkflowNodes(), jsonWorkflowDescription);
        RequestContext.getCurrentInstance().execute("f_setValidationMessage(\"" + workflowHandler.getValidationMessage() + "\")");
    }

    private Map<String,IWorkflowNode> initWorkflowNodes() {
        Map<String,IWorkflowNode> nodes = new HashMap<>();
        //add current WPS proxy
        if(this.getHandler() != null)
            nodes.put(this.getHandler().getProxy().getIdentifier().toString(), this.getHandler().getProxy());
        //add selected WFS proxies
        for(Map.Entry<String,WFSProxy> wfs : this.getWFSProxies().entrySet()){
            nodes.put(wfs.getKey(), wfs.getValue());
        }
        return nodes;
    }

    private Map<String,WFSProxy> getWFSProxies() {
        Layer layer = this.getLayer();
        Map<String,WFSProxy> proxies = new HashMap<>();
        for(AbstractOWSHandler handler : layer.getSelectedOWSHandler()){
            proxies.put(handler.getIdentifier(), (WFSProxy) handler.getProxy());
        }
        return proxies;
    }

    @ManagedProperty(value="#{layer}")
    private Layer layer;
    public Layer getLayer() {
        return this.layer;
    }
    public void setLayer(Layer layer)   {
        this.layer = layer;
    }

    public boolean validWorkflow() {
        return this.workflowHandler != null && this.workflowHandler.isValidDescription();
    }

    public void executeProcess() {
        if(!this.workflowHandler.isValidDescription()){
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "Validation error", "WPS connections are not valid");
            return;
        }
        //TODO trigger execution and return result to client
    }

}
