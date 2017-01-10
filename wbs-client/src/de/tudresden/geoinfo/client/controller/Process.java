package de.tudresden.geoinfo.client.controller;

import de.tudresden.geoinfo.client.handler.ProcessDelegator;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@SessionScoped
public class Process implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String SIN = "Sinuosity Matrix";
	private String ZON = "Zonal Statistics";

	private String selectedProcess;
	private String[] processes;
	
	private String selectedFeature;
	
	private String jsonFeatures;
	
	@PostConstruct
    public void init() {
		processes = new String[]{SIN, ZON};
	}
	
	public String getSelectedFeature() {
		return selectedFeature;
	}
	
	public void setSelectedFeature(String selectedFeature) {
		this.selectedFeature = selectedFeature;
	}
	
	public String[] getProcesses() {
        return processes;
    }
	
	public String getSelectedProcess() {
        return selectedProcess;
    }
	
	public void setSelectedProcess(String selectedProcess) {
		this.selectedProcess = selectedProcess;
    }
	
	public void initFeatures() throws FileNotFoundException {
		FacesContext context = FacesContext.getCurrentInstance();
	    Map<String,String> map = context.getExternalContext().getRequestParameterMap();
	    this.jsonFeatures = map.get("features").replace("\\\"", "\"").replaceAll("^\"+", "").replaceAll("\"+$", "");
    }
	
	public void executeProcess() {
		ProcessDelegator delegator = new ProcessDelegator(getSelectedProcess(), jsonFeatures);
    }	
	
}
