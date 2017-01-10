package de.tudresden.geoinfo.client.controller;

import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@SessionScoped
public class Overlay implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String GNET = "River Network";
	private final String SGUT = "WFD Quality Classification";
	private final String UEG = "Designated Flood Plains";
    private final String COB = "COBWEB Observations";

	private String[] layers, selection;
	private Map<String,String> features;

	@PostConstruct
    public void init() {
		layers = new String[]{GNET, SGUT, UEG, COB};
	}
	
	public String[] getLayers() {
        return layers;
    }
	
	public void setSelection(String[] selection) {
		this.selection = selection;
		update();
    }
	
	public String[] getSelection() {
        return selection;
    }
	
	public void update(){
		//update layer view
		StringBuilder sLayers = new StringBuilder();
		if(selection != null && selection.length > 0){
			for(String overlay : getSelection()){
				sLayers.append(overlay + ";");
			}
		}
		//call javascript update
		if(sLayers.length() > 0)
			RequestContext.getCurrentInstance().execute("olMap.updateOverlays('" + sLayers.substring(0, sLayers.length()-1).toString() + "')");
	}
	
}
