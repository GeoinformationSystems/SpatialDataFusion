package de.tudresden.gis.fusion.client.ows;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.context.RequestContext;
import de.tudresden.gis.fusion.client.ows.document.WPSCapabilities;
import de.tudresden.gis.fusion.client.ows.document.WPSProcessDescriptions;

public class WPSHandler extends OWSHandler {

	private static final long serialVersionUID = 1L;
	
	private final String SERVICE = "wps";	
	private final String REQUEST_DESCRIBEPROCESS = "describeProcess";
	private final String PARAM_IDENTIFIER = "identifier";	
	private final String DEFAULT_VERSION = "1.0.0";
	
	public WPSHandler(int id) {
		this.setId(id);
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
	}
	
	private int id;
	public int getId(){ return id; }
	public void setId(int id){ this.id = id; }

	WPSCapabilities capabilities;
	WPSProcessDescriptions descriptions;
	public void initCapabilities() {
		try {
			this.emptySelectedProcesses();
			//get capabilities document
			capabilities = new WPSCapabilities(this.getGetCapabilitiesRequest());
		} catch (Exception e) {
			//display error message and return
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load capabilities from server (" + e.getLocalizedMessage() + ")");
			return;
		}
		
		//retreive process descriptions
		try {
			this.setParameter(PARAM_REQUEST, REQUEST_DESCRIBEPROCESS);
			this.setParameter(PARAM_IDENTIFIER, identifier2String(capabilities.getWPSProcesses()));
			String request = this.getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_IDENTIFIER}, new String[]{});
			//get process descriptions
			descriptions = new WPSProcessDescriptions(request);
		} catch (Exception e) {
			//display error message and return
			e.printStackTrace();
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load process descriptions from server (" + e.getLocalizedMessage() + ")");
			return;
		}
		
		//display success message
		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "loaded capabilities and process descriptions from server");
	}

	private String identifier2String(Set<String> identifier){
		StringBuilder builder = new StringBuilder();
		for(String id : identifier){
			builder.append(id + ",");
		}
		return builder.substring(0, builder.length() - 1); //remove last comma
	}
	
	/**
	 * call javascript function that updates jsPlumb processes
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void updateProcesses(ValueChangeEvent event) {
		HashSet<String> oldValues = (HashSet<String>) event.getOldValue();
		HashSet<String> newValues = (HashSet<String>) event.getNewValue();
		removeProcesses(oldValues, newValues);
		addProcesses(oldValues, newValues);
	}
	
	private void removeProcesses(Set<String> oldValues, Set<String> newValues) {
		for(String oldValue : oldValues){
			if(!newValues.contains(oldValue))
				RequestContext.getCurrentInstance().execute("f_removeProcess('" + oldValue + "')");
		}	
	}
	
	private void addProcesses(Set<String> oldValues, Set<String> newValues) {
		for(String newValue : newValues){
			if(!oldValues.contains(newValue))
				RequestContext.getCurrentInstance().execute("f_addProcessfromJSON('" + getJSONDescription(newValue) + "')");
		}
	}

	/**
	 * get JSON description for single process
	 * @param process process name
	 * @return JSON description
	 */
	public String getJSONDescription(String process) {
		if(process == null || descriptions == null)
			return "";
		return descriptions.getJSONDescription(process);
	}
	
	private Set<String> selectedProcesses = new HashSet<String>();
	public Set<String> getSelectedProcesses(){ return selectedProcesses; }
	public void setSelectedProcesses(Set<String> processes){ this.selectedProcesses = processes; }
	public void emptySelectedProcesses() { selectedProcesses.clear(); }
	
	public Map<String,String> getProcessDescriptions() { 
		Map<String,String> map = new LinkedHashMap<String,String>();
		if(descriptions != null){
			for(String identifier : descriptions.getProcessIdentifier()){
				map.put(identifier, getProcessDescription4Display(identifier));
			}	
		}
		return map;
	}
	
	private String getProcessDescription4Display(String identifier){
		String description = descriptions.getDescription(identifier);
		return "<span class=\"ui-state-hover\">" + identifier + "</span> " + (description != null ? description : "no description available");
	}
	
	public String getProcessShort(String name) {
		if(descriptions == null || !descriptions.getProcessIdentifier().contains(name))
			return null;
		String description = descriptions.getDescription(name);
		return name + " (" + description + ")";
	}

	public String getIdentifier() { return this.getParameter(PARAM_IDENTIFIER); }
	public void setIdentifier(String value) { this.setParameter(PARAM_IDENTIFIER, value); }

}
