package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.Serializable;
import java.util.Set;

import de.tudresden.gis.fusion.client.ows.document.desc.IOFormat;

public class IOConnection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private IONode start;
	private IONode end;
	
	public IOConnection(IONode start, IONode end){
		//set nodes
		this.setStart(start);
		this.setEnd(end);
		//set node connection
		start.addOutgoingConnection(this);
		end.setIncommingConnection(this);
		//set process connection
		start.getProcess().addSuccessor(end.getProcess());
		end.getProcess().addAncestor(start.getProcess());
	}

	public IONode getStart() { return start; }
	public void setStart(IONode start) { this.start = start; }

	public IONode getEnd() { return end; }
	public void setEnd(IONode end) { this.end = end; }
	
	/**
	 * get start process
	 * @return start process
	 */
	public IOProcess getStartProcess() {
		return getStart().getProcess();
	}
	
	/**
	 * get end process
	 * @return end process
	 */
	public IOProcess getEndProcess() {
		return getEnd().getProcess();
	}
	
	/**
	 * check if connection is valid
	 * @return true, if valid
	 */
	public boolean isValid() {
		return !this.selfConnect() && this.haveCommonFormat(start.getSupportedFormats(), end.getSupportedFormats());
	}
	
	/**
	 * check, if connection is connecting the same process
	 * @return true, if connection is only within an io process
	 */
	private boolean selfConnect() {
		boolean selfConnected = getStart().getProcess().equals(getEnd().getProcess());
		if(selfConnected)
			validationMessage = start.getIdentifier() + " and " + end.getIdentifier() + " belong to same process";
		return selfConnected;
	}

	private String validationMessage;
	public String getValidationMessage() { return validationMessage; }
	
	/**
	 * check, if format sets have common format
	 * @param s_formats source format set
	 * @param t_formats target format set
	 * @return true, if sets have at least one format in common
	 */
	private boolean haveCommonFormat(Set<IOFormat> s_formats, Set<IOFormat> t_formats){
		for(IOFormat outputFormat : s_formats){
			for(IOFormat inputFormat : t_formats){
				if(outputFormat.equals(inputFormat)){
					validationMessage = "";
					return true;
				}
			}
		}
		validationMessage = start.getIdentifier() + " and " + end.getIdentifier() + " have no common format";
		return false;
	}

}
