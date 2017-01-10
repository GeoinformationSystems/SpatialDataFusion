package de.tudresden.geoinfo.client.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class Pattern implements Serializable {

	private static final long serialVersionUID = 1L;

	private String CON = "Observation Connectivity";

	private String selectedPattern;
	private String[] patterns;

	private String selectedFeature;

	private String jsonFeatures;

	@PostConstruct
	public void init() {
		patterns = new String[]{CON};
	}

	public String getSelectedFeature() {
		return selectedFeature;
	}

	public void setSelectedFeature(String selectedFeature) {
		this.selectedFeature = selectedFeature;
	}

	public String[] getPatterns() {
		return patterns;
	}

	public String getSelectedPattern() {
		return selectedPattern;
	}

	public void setSelectedPattern(String selectedPattern) {
		this.selectedPattern = selectedPattern;
	}

}
