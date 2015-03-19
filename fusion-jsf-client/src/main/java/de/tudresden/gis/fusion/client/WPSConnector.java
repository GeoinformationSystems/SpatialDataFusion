package de.tudresden.gis.fusion.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.tudresden.gis.fusion.client.ows.WPSHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.ConnectionHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.WPSOrchestration;

@ManagedBean(name = "wpsConnector")
@SessionScoped
public class WPSConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PostConstruct
	public void init() {
		this.addWPSHandler();
	}
	
	private SortedMap<Integer,WPSHandler> wpsHandlers = new TreeMap<Integer,WPSHandler>();
	public List<WPSHandler> getHandlers() { return new ArrayList<WPSHandler>(wpsHandlers.values()); }
	
	public int getNumberOfHandlers() { return wpsHandlers.size(); }
	
	public void execute() {
		if(connectionHandler == null || connectionInvalid)
			return;
		try {
			WPSOrchestration orchestration = new WPSOrchestration(connectionHandler);
			orchestration.execute();
			setIsNotExecuted(false);
		} catch (IOException ioe) {
			setIsNotExecuted(true);
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not perform process: " + ioe.getLocalizedMessage());
		}
	}
	
	/**
	 * uncheck selected processes
	 */
	public void reset(){
		for(WPSHandler handler : getHandlers()){
			handler.emptySelectedProcesses();
		}
		setValidationMessage(null);
	}
	
	private String connections;
	public String getConnections() { return connections; }
	public void setConnections(String connections) { 
		if(connections == null || connections.length() == 0)
			return;
		this.connections = connections;
		this.validateConnections();
	}
	
	ConnectionHandler connectionHandler;
	public void validateConnections() {
		try {
			connectionHandler = new ConnectionHandler(getConnections());
			setConnectionInvalid(!connectionHandler.isValid());
			setValidationMessage(connectionHandler.validationMessage());
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			setConnectionInvalid(true);
		}
	}
	
	private String validationMessage;
	public String getValidationMessage() { return validationMessage; }
	public void setValidationMessage(String validationMessage) { 
		this.validationMessage = validationMessage;
	}
	
	private boolean isNotExecuted = true;
	public boolean getIsNotExecuted() { return isNotExecuted; }
	public void setIsNotExecuted(boolean isNotExecuted) { this.isNotExecuted = isNotExecuted; }
	
	public boolean connectionInvalid = true;
	public boolean getConnectionInvalid() { return connectionInvalid; }
	public void setConnectionInvalid(boolean connectionInvalid) { this.connectionInvalid = connectionInvalid; }
	
	/**
	 * add WPS Handler (called by 'Add WPS')
	 */
	public void addWPSHandler() {
		int currId = getLastId() + 1;
		wpsHandlers.put(currId, new WPSHandler(currId));
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
