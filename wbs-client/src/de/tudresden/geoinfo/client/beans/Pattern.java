package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.WPSHandler;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ManagedBean
@SessionScoped
public class Pattern implements Serializable {

	private static final long serialVersionUID = 1L;

    private Map<String,WPSHandler> gppHandler;
    private Set<String> patterns;
    private String selection;

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    public void addPattern(String pattern) {
        if(this.patterns == null)
            this.patterns = new HashSet<>();
        this.patterns.add(pattern);
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

//	private String CON = "Observation Connectivity";
//
//	private String selectedPattern;
//	private String[] patterns;
//
//	private String selectedFeature;
//
//	private String jsonFeatures;
//
//	@PostConstruct
//	public void init() {
//		patterns = new String[]{CON};
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
//	public String[] getPatterns() {
//		return patterns;
//	}
//
//	public String getSelectedPattern() {
//		return selectedPattern;
//	}
//
//	public void setSelectedPattern(String selectedPattern) {
//		this.selectedPattern = selectedPattern;
//	}

}