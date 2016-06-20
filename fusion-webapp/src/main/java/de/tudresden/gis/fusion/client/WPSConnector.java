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

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import de.tudresden.gis.fusion.client.ows.WPSHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.BPMNModel;
import de.tudresden.gis.fusion.client.ows.orchestration.ConnectionHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.IOProcess;
import de.tudresden.gis.fusion.client.ows.orchestration.WPSOrchestration;

@ManagedBean(name = "wpsConnector")
@SessionScoped
public class WPSConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	private BPMNModel bpmnModel;
	private ConnectionHandler connectionHandler;
	
	@PostConstruct
	public void init() {
		this.bpmnModel = new BPMNModel();
		this.connectionHandler = new ConnectionHandler();
		this.addWPSHandler();
		this.reset();
	}
	
	@ManagedProperty(value="#{referenceWFS}")
    private ReferenceWFS referenceWFS;
    public ReferenceWFS getReferenceWFS() { return referenceWFS; }
    public void setReferenceWFS(ReferenceWFS referenceWFS) { this.referenceWFS = referenceWFS; }
    
    @ManagedProperty(value="#{targetWFS}")
    private TargetWFS targetWFS;
    public TargetWFS getTargetWFS() { return targetWFS; }
    public void setTargetWFS(TargetWFS targetWFS) { this.targetWFS = targetWFS; }
    
    @ManagedProperty(value="#{outputConnector}")
    private OutputConnector outputConnector;
    public OutputConnector getOutputConnector() { return outputConnector; }
    public void setOutputConnector(OutputConnector outputConnector) { this.outputConnector = outputConnector; }
	
	private SortedMap<Integer,WPSHandler> wpsHandlers = new TreeMap<Integer,WPSHandler>();
	public List<WPSHandler> getHandlers() { return new ArrayList<WPSHandler>(wpsHandlers.values()); }
	
	/**
	 * get all io process definitions
	 * @return available io process definitions
	 * @throws IOException 
	 */
	private Map<String,IOProcess> getIOProcesses() throws IOException{
		Map<String,IOProcess> ioProcesses = new HashMap<String,IOProcess>();
		//add wps processes
		if(wpsHandlers != null && wpsHandlers.size() > 0){
			for(WPSHandler wpsHandler : wpsHandlers.values()){
				Set<IOProcess> processes = wpsHandler.getIOProcesses();
				if(processes != null && processes.size() > 0){
					for(IOProcess process : wpsHandler.getIOProcesses()){
						ioProcesses.put(process.getUUID(), process);
					}
				}
			}
		}
		//add wfs
		ioProcesses.put(referenceWFS.getIOProcess().getUUID(), referenceWFS.getIOProcess());
		ioProcesses.put(targetWFS.getIOProcess().getUUID(), targetWFS.getIOProcess());
		//add storage process
		ioProcesses.put(outputConnector.getIOProcess().getUUID(), outputConnector.getIOProcess());
		
		return ioProcesses;
	}
	
	public int getNumberOfHandlers() { return wpsHandlers.size(); }
	
	public void execute() {
		if(connectionHandler == null || connectionInvalid)
			return;
		try {
			WPSOrchestration orchestration = new WPSOrchestration(getBpmnXML());
			boolean success = orchestration.execute();
			setIsNotExecuted(!success);
			if(success)
				this.sendMessage(FacesMessage.SEVERITY_INFO, "Success", "Successfully performed defined process");
			else
				this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not perform the defined process");
		} catch (IOException ioe) {
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not perform process: " + ioe.getLocalizedMessage());
		}
	}
	
	private boolean isNotExecuted = true;
	public void setIsNotExecuted(boolean isNotExecuted) { this.isNotExecuted = isNotExecuted; }
	public boolean getIsNotExecuted() { return isNotExecuted; }
	
	/**
	 * update BPMN model
	 */
	public void updateBPMN(){
		try {
			this.bpmnModel.initModel(this.connectionHandler);
			setBpmnXML(this.bpmnModel.asXML());
		} catch (IOException ioe) {
			setBpmnXML("Could not generate valid BPMN model");
		}
	}
	
	private String bpmnXML;
	public void setBpmnXML(String bpmnXML) { this.bpmnXML = bpmnXML; }
	public String getBpmnXML() { 
		updateBPMN();
		return bpmnXML; 
	}
	
	/**
	 * get BPMN XML as file download
	 * @return BPMN XML file
	 */
	public StreamedContent getBpmnXMLFile() {
		return new ByteArrayContent(getBpmnXML().getBytes(), "text/xml", "bpmnXML.xml");
	}
	
	/**
	 * uncheck selected processes
	 * @throws IOException 
	 */
	public void reset() {
		for(WPSHandler handler : getHandlers()){
			handler.emptySelectedProcesses();
		}
		this.setConnections(null);
		this.setIsNotExecuted(true);
	}
	
	private String connections;
	public String getConnections() { return connections; }
	public void setConnections(String connections) {
		if(connections == null || connections.isEmpty() || connections.matches("\\[\\s*\\]"))
			this.connections = "";
		else
			this.connections = connections;
		try {
			this.initConnectionHandler();
			this.validateConnections();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initConnectionHandler() throws IOException {
		this.connectionHandler.initConnections(this.getIOProcesses(), this.getConnections());
	}
	private void validateConnections() {
		this.connectionHandler.validate();
		setConnectionInvalid(!connectionHandler.isValid());
		setValidationMessage(connectionHandler.validationMessage());
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
