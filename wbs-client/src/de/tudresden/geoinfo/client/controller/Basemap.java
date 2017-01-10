package de.tudresden.geoinfo.client.controller;

import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class Basemap implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String OSM = "OpenStreetMap";
    private final String CLC = "Corine Land Cover";
	private final String DGM = "Digital Elevation Model";

	private String[] layers, selection = {OSM};
	
	@PostConstruct
    public void init() {
		layers = new String[]{OSM, CLC, DGM};
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
			for(String layer : getSelection()){
				sLayers.append(layer + ";");
			}
		}
		//call javascript update
		if(sLayers.length() > 0)
			RequestContext.getCurrentInstance().execute("olMap.updateBasemap('" + sLayers.substring(0, sLayers.length()-1).toString() + "')");
	}
	
}
