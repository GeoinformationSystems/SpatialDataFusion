package de.tudresden.gis.fusion.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import de.tudresden.gis.fusion.client.ows.WPSHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.BPMNModel;
import de.tudresden.gis.fusion.client.ows.orchestration.ConnectionHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.IOProcess;
import de.tudresden.gis.fusion.client.ows.orchestration.WPSOrchestration;

@ManagedBean(name = "wpsConnector")
@SessionScoped
public class WPSConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PostConstruct
	public void init() {
		this.addWPSHandler();
	}
	
	@ManagedProperty(value="#{referenceWFS}")
    private ReferenceWFS referenceWFS;
    public ReferenceWFS getReferenceWFS() { return referenceWFS; }
    public void setReferenceWFS(ReferenceWFS referenceWFS) { this.referenceWFS = referenceWFS; }
    
    @ManagedProperty(value="#{targetWFS}")
    private TargetWFS targetWFS;
    public TargetWFS getTargetWFS() { return targetWFS; }
    public void setTargetWFS(TargetWFS targetWFS) { this.targetWFS = targetWFS; }
    
    @ManagedProperty(value="#{fusekiConnector}")
    private FusekiConnector fusekiConnector;
    public FusekiConnector getFusekiConnector() { return fusekiConnector; }
    public void setFusekiConnector(FusekiConnector fusekiConnector) { this.fusekiConnector = fusekiConnector; }
	
	private SortedMap<Integer,WPSHandler> wpsHandlers = new TreeMap<Integer,WPSHandler>();
	public List<WPSHandler> getHandlers() { return new ArrayList<WPSHandler>(wpsHandlers.values()); }
	
	/**
	 * get all io process definitions
	 * @return available io process definitions
	 */
	private Map<String,IOProcess> getIOProcesses(){
		Map<String,IOProcess> ioProcesses = new HashMap<String,IOProcess>();
		//add wps processes
		if(wpsHandlers != null && wpsHandlers.size() > 0){
			for(WPSHandler wpsHandler : wpsHandlers.values()){
				Set<IOProcess> processes = wpsHandler.getIOProcesses();
				if(processes != null && processes.size() > 0){
					for(IOProcess process : wpsHandler.getIOProcesses()){
						ioProcesses.put(wpsHandler.getId() + "_" + process.getLocalIdentifier(), process);
					}
				}
			}
		}
		//add wfs
		ioProcesses.put("0_ReferenceWFS_WFS_GML", referenceWFS.getIOProcess());
		ioProcesses.put("0_TargetWFS_WFS_GML", targetWFS.getIOProcess());
		//add storage process
		ioProcesses.put("0_OutputRelations", fusekiConnector.getIOProcess());
		
		return ioProcesses;
	}
	
	public int getNumberOfHandlers() { return wpsHandlers.size(); }
	
	public void execute() {
		if(connectionHandler == null || connectionInvalid)
			return;
		try {
			WPSOrchestration orchestration = new WPSOrchestration(connectionHandler);
			orchestration.execute();
		} catch (IOException ioe) {
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not perform process: " + ioe.getLocalizedMessage());
		}
	}
	
	/**
	 * update BPMN
	 */
	public void updateBPMN(){
		if(connectionHandler == null || connectionInvalid){
			this.sendMessage(FacesMessage.SEVERITY_WARN, "Warn", "BPMN cannot be created from current model");
			setBpmnXML(null);
			return;
		}
		try {
			setBpmnXML(new BPMNModel(connectionHandler).asXML());
		} catch (IOException ioe){
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error while creating BPMN XML from current model");
			setBpmnXML(null);
		}
	}
	
	private String bpmnXML;
	public void setBpmnXML(String bpmnXML) { this.bpmnXML = bpmnXML; }
	public String getBpmnXML() { 
		updateBPMN();
		return bpmnXML; 
	}
	
	/**
	 * display BPMN XML
	 */
	public void showBPMN() {
		Map<String,Object> options = new HashMap<String, Object>();
        options.put("contentWidth", 800);
        options.put("contentHeight", 600);
		RequestContext.getCurrentInstance().openDialog("bpmnXML", options, null);
	}
	
	/**
	 * uncheck selected processes
	 */
	public void reset(){
		for(WPSHandler handler : getHandlers()){
			handler.emptySelectedProcesses();
		}
		setValidationMessage(null);
		setConnectionInvalid(true);
	}
	
	private String connections;
	public String getConnections() { return connections; }
	public void setConnections(String connections) {
		if(connections == null || connections.length() <= 2){
			setValidationMessage(null);
			return;
		}
		this.connections = connections;
		this.validateConnections();
	}
	
	ConnectionHandler connectionHandler;
	private void validateConnections() {
		try {
			connectionHandler = new ConnectionHandler(this.getIOProcesses(), this.getConnections());
			setConnectionInvalid(!connectionHandler.isValid());
			setValidationMessage(connectionHandler.validationMessage());
		} catch (IllegalArgumentException | IOException e) {
			//do nothing
			setConnectionInvalid(true);
		}
	}
	
	private String validationMessage;
	public String getValidationMessage() { return validationMessage; }
	public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }
	
	public boolean connectionInvalid = true;
	public boolean getConnectionInvalid() { return connectionInvalid; }
	public void setConnectionInvalid(boolean connectionInvalid) { this.connectionInvalid = connectionInvalid; }
	
	/**
	 * add WPS Handler (called by 'Add WPS')
	 */
	public void addWPSHandler() {
		int currId = getLastId() + 1;
		wpsHandlers.put(currId, new WPSHandler(String.valueOf(currId)));
	}
	
	/**
	 * get id of last inserted Handler
	 * @return id of last handler in List
	 */
	public int getLastId() {
		int max = 0;
		for(int i : wpsHandlers.keySet()){
			if(i > max)
				max = i;
		}
		return max;
	}
	
	/**
	 * remove Handler with selected id
	 * @param id id of the handler to be removed
	 */
	public void removeWPSHandler(int id) {
		wpsHandlers.remove(id);
	}
	
	/**
	 * append message to faces context
	 * @param severity message severity level
	 * @param summary message string
	 * @param detail detailed message string
	 */
	protected void sendMessage(Severity severity, String summary, String detail){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(severity, summary, detail) );		
	}
}
