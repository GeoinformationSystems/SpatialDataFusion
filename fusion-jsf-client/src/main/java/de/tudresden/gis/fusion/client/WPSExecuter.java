package de.tudresden.gis.fusion.client;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONException;

import de.tudresden.gis.fusion.client.ows.WFSHandler;
import de.tudresden.gis.fusion.client.ows.WPSHandler;
import de.tudresden.gis.fusion.client.ows.orchestration.WPSOrchestration;

@ManagedBean(name = "wpsExecute")
@SessionScoped
public class WPSExecuter extends WPSOrchestration {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty("#{referenceWFS}")
	private WFSHandler referenceWFS;
	public WFSHandler getReferenceWFS() { return referenceWFS; }
	public void setReferenceWFS(WFSHandler referenceWFS) { this.referenceWFS = referenceWFS; }
	
	@ManagedProperty("#{targetWFS}")
	private WFSHandler targetWFS;
	public WFSHandler getTargetWFS() { return targetWFS; }
	public void setTargetWFS(WFSHandler targetWFS) { this.targetWFS = targetWFS; }
	
	@ManagedProperty("#{wpsConnection}")
	private WPSHandler wpsConnection;
	public WPSHandler getWpsConnection() { return wpsConnection; }
	public void setWpsConnection(WPSHandler wpsConnection) { this.wpsConnection = wpsConnection; }
	
	public void executeProcess() {
		try {
			super.executeProcess(referenceWFS, targetWFS, wpsConnection);
		} catch (IOException | JSONException e) {
			//display error message
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not execute process");
			e.printStackTrace();
			return;
		}
		//display success message
		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "Process successfully executed");
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
