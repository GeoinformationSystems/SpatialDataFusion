package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.JSONUtils;
import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WorkflowHandler;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.IWorkflowNode;
import de.tudresden.geoinfo.fusion.operation.ows.OWSServiceOperation;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNWorkflow;
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

@ManagedBean(name = "process")
@SessionScoped
public class WPSBean extends AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private WorkflowHandler workflowHandler;
    @ManagedProperty(value = "#{layer}")
    private WFSBean layer;

    @Override
    boolean multiSelect() {
        return false;
    }

    @Override
    WPSProxy initOWSHandler(String uid, String sBaseURL) throws IOException {
        return new WPSProxy(new Identifier(uid), new URLLiteral(sBaseURL));
    }

    @Override
    public void registerOWSOffering(OWSServiceOperation handler, String selectedOffering) {
        //do nothing
    }

    @Override
    void update() {
        //do nothing
    }

    @Override
    public @NotNull WPSProxy getHandler(@NotNull String uid) {
        return (WPSProxy) super.getHandler(uid);
    }

    public @Nullable WPSProxy getHandler() {
        if (this.getSingleSelectedOffering() == null)
            return null;
        return this.getHandler(this.getSingleSelectedOffering());
    }

    public void initJSPlumb() {
        WPSProxy handler = this.getHandler();
        RequestContext.getCurrentInstance().execute("f_initSinglePlumb('" + JSONUtils.getJSONProcessDescription(handler).toString() + "')");
    }

    public void clearJSPlumb() {
        RequestContext.getCurrentInstance().execute("f_clearSinglePlumb()");
        if(this.getHandler() != null)
            this.getHandler().reset();
    }

    public void setWorkflow(final String jsonWorkflowDescription) {
        if (this.workflowHandler == null)
            this.workflowHandler = new WorkflowHandler();
        this.workflowHandler.setWorkflowDescription(initWorkflowNodes(), jsonWorkflowDescription);
        RequestContext.getCurrentInstance().execute("f_setValidationMessage(\"" + workflowHandler.getValidationMessage() + "\")");
    }

    private Map<String, IWorkflowNode> initWorkflowNodes() {
        Map<String, IWorkflowNode> nodes = new HashMap<>();
        //add current WPS proxy
        if (this.getHandler() != null)
            nodes.put(this.getHandler().getIdentifier().toString(), this.getHandler());
        //add selected WFS proxies
        for (Map.Entry<String, WFSProxy> wfs : this.getWFSProxies().entrySet()) {
            nodes.put(wfs.getKey(), wfs.getValue());
        }
        return nodes;
    }

    private Map<String, WFSProxy> getWFSProxies() {
        WFSBean layer = this.getLayer();
        Map<String, WFSProxy> proxies = new HashMap<>();
        for (OWSServiceOperation handler : layer.getSelectedOWSHandler()) {
            proxies.put(handler.getIdentifier().toString(), (WFSProxy) handler);
        }
        return proxies;
    }

    public WFSBean getLayer() {
        return this.layer;
    }

    public void setLayer(WFSBean layer) {
        this.layer = layer;
    }

    public boolean validWorkflow() {
        return this.workflowHandler != null && this.workflowHandler.isValidDescription();
    }

    public void executeProcess() {
        if (!this.workflowHandler.isValidDescription()) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "Validation error", "WPS connections are not valid");
            return;
        }
//        this.workflowHandler.execute();

        //set output
//        MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "Results available", "Results are available on the results menu");
//        RequestContext.getCurrentInstance().execute("f_appendResultMessage(\"" + this.workflowHandler.getResultMessage() + "\")");

        //init workflow
        CamundaBPMNModel model = this.workflowHandler.getBPMNModel();
        CamundaBPMNWorkflow workflow = new CamundaBPMNWorkflow(null, model);

    }

}
