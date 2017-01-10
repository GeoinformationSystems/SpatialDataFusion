package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;

@ManagedBean
public class FusionObjective implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] objectives;
	
	public String[] getObjectives() {
        return objectives;
    }    
 
    public void setObjectives(String objectives[]) {
        this.objectives = objectives;
    }
	
}
