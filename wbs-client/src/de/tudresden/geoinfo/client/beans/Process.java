package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.client.handler.WPSHandler;
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
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;

	private Map<String,WPSHandler> gpHandler;
	private Set<String> processes;
	private String selection;

	public Set<String> getProcesses() {
		return processes;
	}

	public void setProcesses(Set<String> processes) {
		this.processes = processes;
	}

	public void addProcess(String process) {
		if(this.processes == null)
			this.processes = new HashSet<>();
		this.processes.add(process);
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

	public void registerWPSHandler(final String key, final String url, final String sProcess){
		//only add key, if url or sLayer are empty
		if(url == null || url.isEmpty() || sProcess == null || sProcess.isEmpty())
			this.addProcess(key);
		else {
			try {
				WPSHandler handler = new WPSHandler(url);
				handler.setProcess(sProcess);
				this.addWPSHandler(key, handler);
				this.addProcess(key);
			} catch (Exception e) {
				MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "WPS Handler Error", e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	private void addWPSHandler(String key, WPSHandler handler) {
		if(gpHandler == null)
			this.gpHandler = new HashMap<>();
		this.gpHandler.put(key, handler);
	}


	private String tmp_processKey;
	public String getTmp_processKey() {
		return this.tmp_processKey;
	}
	public void setTmp_processKey(String tmp_processKey) {
		this.tmp_processKey = tmp_processKey;
	}

	private String tmp_processUrl;
	public String getTmp_processUrl() {
		return this.tmp_processUrl;
	}
	public void setTmp_processUrl(String tmp_processUrl) {
		this.tmp_processUrl = tmp_processUrl;
	}

	private String tmp_processSelected;
	public String getTmp_processSelected() {
		return this.tmp_processSelected;
	}
	public void setTmp_processSelected(String tmp_processSelected) {
		this.tmp_processSelected = tmp_processSelected;
	}

	private WPSHandler tmp_processHandler;
	public void setTmp_processHandler(WPSHandler tmp_processHandler) {
		this.tmp_processHandler = tmp_processHandler;
	}

	public Set<String> getTmp_processes(){
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
	    if(this.tmp_processUrl == null || this.tmp_processUrl.isEmpty()) {
			MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "No URL", "A WPS endpoint must be provided");
			return;
		}
		//test valid url
		if(!this.tmp_processUrl.matches(URILiteral.getURLRegex())) {
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
        if(this.tmp_processKey == null || this.tmp_processKey.isEmpty())
            this.tmp_processKey = tmp_processSelected;
        try {
            tmp_processHandler.setProcess(tmp_processSelected);
            this.addWPSHandler(tmp_processKey, tmp_processHandler);
            this.addProcess(tmp_processKey);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.tmp_reset();
        }
	}

	public void initJSPlumb() {
        WPSHandler handler = this.getSelectionHandler();
        RequestContext.getCurrentInstance().execute("f_addSingleProcess('" + handler.getJSONProcessDescription().toString() + "')");
    }

    public void clearJSPlumb() {
        RequestContext.getCurrentInstance().execute("f_removeSingleProcess()");
    }

	public void executeProcess() {

    }

//
//
//	private String SIN = "Sinuosity Matrix";
//	private String ZON = "Zonal Statistics";
//
//	private String selectedProcess;
//	private String[] processes;
//
//	private String selectedFeature;
//
//	private String jsonFeatures;
//
//	@PostConstruct
//    public void init() {
//		processes = new String[]{SIN, ZON};
//	}
//
//	public String getSelectedFeature() {
//		return selectedFeature;
//	}
//
//	public void setSelectedFeature(String selectedFeature) {
//		this.selectedFeature = selectedFeature;
//	}
//
//	public String[] getProcesses() {
//        return processes;
//    }
//
//	public String getSelectedProcess() {
//        return selectedProcess;
//    }
//
//	public void setSelectedProcess(String selectedProcess) {
//		this.selectedProcess = selectedProcess;
//    }
//
//	public void initFeatures() throws FileNotFoundException {
//		FacesContext context = FacesContext.getCurrentInstance();
//	    Map<String,String> map = context.getExternalContext().getRequestParameterMap();
//	    this.jsonFeatures = map.get("features").replace("\\\"", "\"").replaceAll("^\"+", "").replaceAll("\"+$", "");
//    }
//
//	public void executeProcess() {
//		ProcessDelegator delegator = new ProcessDelegator(getSelectedProcess(), jsonFeatures);
//    }
	
}
