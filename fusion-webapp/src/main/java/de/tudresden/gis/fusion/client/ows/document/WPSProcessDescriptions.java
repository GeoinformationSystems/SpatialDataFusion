package de.tudresden.gis.fusion.client.ows.document;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class WPSProcessDescriptions extends OWSResponse {
	
	private static final long serialVersionUID = 1L;
	
	private final String PROCESS_DESCRIPTION = ".*(?i)ProcessDescription$";
	
	private Map<String,WPSProcessDescription> wpsProcesses;

	/**
	 * Constructor
	 * @param sRequest service request
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WPSProcessDescriptions(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		super(sRequest);
		wpsProcesses = new HashMap<String,WPSProcessDescription>();
		parseDescriptions();
	}

	/**
	 * parse WPS process description
	 */
	private void parseDescriptions() {
		List<Node> matches = this.getNodes(PROCESS_DESCRIPTION);
		for(Node description : matches) {
			WPSProcessDescription process = new WPSProcessDescription(description);
			if(process != null)
				wpsProcesses.put(process.getIdentifier(), process);
		}
	}
	
	/**
	 * get process description as JSON, used for jsPlumb
	 * @param process input process
	 * @return JSON process descriptions
	 */
	public String getJSONDescription(String process) {
		if(process == null || !wpsProcesses.containsKey(process))
			return null;
		return wpsProcesses.get(process).getJSONDescription();
	}
	
	/**
	 * get process descriptions as JSON, used for jsPlumb
	 * @return JSON process descriptions
	 */
	public String getJSONDescription(Set<String> processes) {
		if(processes == null || processes.isEmpty())
			return null;
		
		StringBuilder builder = new StringBuilder("{ \"descriptions\" : [");
		for(String process : processes){
			if(wpsProcesses.containsKey(process))
				builder.append(wpsProcesses.get(process).getJSONDescription() + ",");
		}
		builder.deleteCharAt(builder.length() - 1); //removes last comma
		builder.append("] }");
		return builder.toString();
	}
	
	/**
	 * get process names
	 * @return process names
	 */
	public Set<String> getProcessIdentifier() {
		return wpsProcesses.keySet();
	}
	
	/**
	 * get process description
	 * @param identifier process identifier
	 * @return process description
	 */
	public String getDescription(String identifier) {
		if(wpsProcesses.get(identifier) != null)
			return wpsProcesses.get(identifier).getDescription();
		return null;
	}
	
	public Collection<WPSProcessDescription> getProcessDescriptions() {
		return wpsProcesses.values();
	}
	
	public WPSProcessDescription getProcessDescription(String identifier) {
		return wpsProcesses.get(identifier);
	}

}
