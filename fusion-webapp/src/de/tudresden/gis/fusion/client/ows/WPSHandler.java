package de.tudresden.gis.fusion.client.ows;

import de.tudresden.gis.fusion.client.ows.document.WPSCapabilities;
import de.tudresden.gis.fusion.client.ows.document.WPSProcessDescription;
import de.tudresden.gis.fusion.client.ows.document.WPSProcessDescriptions;
import de.tudresden.gis.fusion.client.ows.orchestration.IOProcess;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import java.util.*;

public class WPSHandler extends OWSHandler {

	private static final long serialVersionUID = 1L;
	
	private final String SERVICE = "wps";	
	private final String REQUEST_DESCRIBEPROCESS = "describeProcess";
	private final String PARAM_IDENTIFIER = "identifier";	
	private final String DEFAULT_VERSION = "1.0.0";
	
	public WPSHandler(String id) {
		this.setId(id);
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
	}
	
	private String id;
	public String getId(){ return id; }
	public void setId(String id){ this.id = id; }

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
		
		//retrieve process descriptions
		try {
			this.setParameter(PARAM_REQUEST, REQUEST_DESCRIBEPROCESS);
			this.setParameter(PARAM_IDENTIFIER, identifier2String(capabilities.getWPSProcesses()));
			String request = this.getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_IDENTIFIER}, new String[]{});
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
			builder.append(id).append(",");
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
	
	/**
	 * get process description for specified identifier
	 * @param identifier identifier
	 * @return process description
	 */
	public WPSProcessDescription getProcessDescription(String identifier){
		if(descriptions != null)
			return descriptions.getProcessDescription(identifier);
		return null;
	}
	
	public Map<String,String> getProcessDescriptions4Display() { 
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
		return identifier + (description != null ? description : "no description available");
	}
	
	public String getProcessShort(String name) {
		if(descriptions == null || !descriptions.getProcessIdentifier().contains(name))
			return null;
		String description = descriptions.getDescription(name);
		return name + " (" + description + ")";
	}

	public String getIdentifier() { return this.getParameter(PARAM_IDENTIFIER); }
	public void setIdentifier(String value) { this.setParameter(PARAM_IDENTIFIER, value); }
	
	/**
	 * get selected processes as IOProcess for chaining purposes
	 * @return io processes
	 */
	public Set<IOProcess> getIOProcesses(){
		if(descriptions == null)
			return null;
		Set<IOProcess> processes = new HashSet<IOProcess>();
		for(WPSProcessDescription description : descriptions.getProcessDescriptions()){
			if(this.getSelectedProcesses().contains(description.getIdentifier())){
				Map<String,String> properties = new HashMap<String,String>();
				properties.put("base", this.getBaseURL());
				properties.put(PARAM_IDENTIFIER, description.getIdentifier());
				properties.put(PARAM_VERSION, this.getVersion());
				processes.add(new IOProcess(SERVICE, description.getUUID(), properties, description.getIONodes()));
			}
		}
		return processes;
	}

}
