package de.tudresden.gis.fusion.client.ows;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WPSHandler extends OWSHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<WPSProcess> processes = new HashSet<WPSProcess>();
	
	private final String SERVICE = "wps";	
	private final String REQUEST_DESCRIBEPROCESS = "describeProcess";
	private final String PARAM_IDENTIFIER = "identifier";	
	private final String DEFAULT_VERSION = "1.0.0";
	
	@PostConstruct
	public void init(){
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
	}
	
	/**
	 * get describeFeatureType request for sleected typename
	 * @return describeFeatureType request
	 * @throws IOException
	 */
	public String getDescribeProcessRequest() throws IOException {
		this.setRequest(REQUEST_DESCRIBEPROCESS);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST,PARAM_VERSION,PARAM_IDENTIFIER}, new String[]{});
	}
	
	/**
	 * init WPS capabilities
	 */
	public void initCapabilities() {
		//retrieve layer information from WFS
		try {
			//get capabilities document
			Document capabilities = this.getCapabilities();
			//get processes
			List<Node> matches = this.getElementsByRegEx(capabilities.getChildNodes(), ".*(?i)Process$", new ArrayList<Node>());
			for(Node node : matches) {
				WPSProcess process = new WPSProcess(node);
				if(process.getIdentifier() != null)
					processes.add(process);
			}
		} catch (Exception e) {
			//display error message and return
			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "could not load capabilities from server (" + e.getLocalizedMessage() + ")");
			return;
		}
		//display success message
		this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "loaded capabilities from server");
	}
	
	public Map<String,String> getSupportedProcesses() { 
		Map<String,String> identifiers = new LinkedHashMap<String,String>();
		for(WPSProcess process : processes){
			identifiers.put(process.getName(), process.getIdentifier());
		}
		return identifiers;
	}
	
	/**
	 * get layer by selected typename
	 * @return layer with selected typename or null, if no typename is selected
	 */
	private WPSProcess getProcess(String identifier){
		if(identifier == null) return null;
		for(WPSProcess process : processes){
			if(process.getIdentifier().equalsIgnoreCase(identifier))
				return process;
		}
		return null;
	}
	
	private Set<String> selectedProcesses = new HashSet<String>();
	public Set<String> getSelectedProcesses(){ return selectedProcesses; }
	public void setSelectedProcesses(Set<String> processes){ this.selectedProcesses = processes; }

	public String getIdentifier() { return this.getParameter(PARAM_IDENTIFIER); }
	public void setIdentifier(String value) { this.setParameter(PARAM_IDENTIFIER, value); }
	
	/**
	 * wps process description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class WPSProcess implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String identifier;
		
		public WPSProcess(Node processNode) {
			//get child nodes
			NodeList layerNodes = processNode.getChildNodes();
			//iterate
			for (int i=0; i<layerNodes.getLength(); i++) {
				//get element node
				Node element = layerNodes.item(i);
				//check for identifier
				if(element.getNodeName().matches(".*(?i)Identifier")){
					this.setIdentifier(element.getTextContent().trim());
				}
			}
		}
		
		public String getIdentifier() { return identifier; }
		public void setIdentifier(String identifier){ this.identifier = identifier; }
		
		public String getName() { 
			if(getIdentifier() == null) return null;
			String[] idSplit = getIdentifier().split("\\.");
			return idSplit[idSplit.length - 1];
		}
		
	}

}
