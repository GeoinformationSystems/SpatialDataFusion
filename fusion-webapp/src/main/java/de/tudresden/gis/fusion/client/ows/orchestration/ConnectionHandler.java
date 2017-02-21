//package de.tudresden.gis.fusion.client.ows.orchestration;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayDeque;
//import java.util.Collections;
//import java.util.Deque;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import org.primefaces.json.JSONArray;
//import org.primefaces.json.JSONException;
//import org.primefaces.json.JSONObject;
//
//import de.tudresden.gis.fusion.client.ows.document.desc.IOFormat;
//import de.tudresden.gis.fusion.client.ows.workflow.IONode.NodeType;
//
//public class ConnectionHandler implements Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//	private Map<String,IOProcess> ioProcesses;
//	private Set<IOConnection> connections;
//	private String validationMessages;
//
//	public void initConnections(Map<String,IOProcess> ioProcesses, String sConnections) {
//		this.ioProcesses = ioProcesses;
//		this.connections = new HashSet<IOConnection>();
//		//return if no connections are set
//		if(sConnections == null || sConnections.isEmpty())
//			return;
//		//try to parse connections from JSON
//		try {
//			JSONArray jConnections = new JSONArray(sConnections);
//			for(int i=0; i<jConnections.length(); i++){
//				IOConnection connection = getIOConnection(ioProcesses, jConnections.getJSONObject(i));
//				connections.add(connection);
//			}
//		} catch (JSONException | IOException e) {
//			this.validationMessages += "Error while parsing connections from JSON: " + e.getLocalizedMessage() + "<br/>";
//		}
//	}
//
//	private final String SOURCE_ID = "s_identifier";
//	private final String TARGET_ID = "t_identifier";
//	private final String SOURCE_OUT = "s_output";
//	private final String TARGET_IN = "t_input";
//
//	/**
//	 * get IO connections for process chaining
//	 * @param ioProcesses available io processes
//	 * @param jConnection JSON connections
//	 * @return IO processes defined by connections
//	 * @throws JSONException
//	 * @throws IOException
//	 */
//	private IOConnection getIOConnection(Map<String, IOProcess> ioProcesses, JSONObject jConnection) throws JSONException, IOException {
//		//get source node
//		String s_id = jConnection.getString(SOURCE_ID);
//		String s_output = jConnection.getString(SOURCE_OUT);
//		//add literal output, if required
//		if(isLiteralOutput(s_id) && !ioProcesses.containsKey(s_id))
//			ioProcesses.put(s_id, getLiteral(s_id));
//		IONode s_node = getNode(s_id, s_output, ioProcesses);
//		//get target node
//		String t_id = jConnection.getString(TARGET_ID);
//		String t_input = jConnection.getString(TARGET_IN);
//		IONode t_node = getNode(t_id, t_input, ioProcesses);
//		//set connection
//		return new IOConnection(s_node, t_node);
//	}
//
//	/**
//	 * check, if identifier is literal (starts with 0_VALUE)
//	 * @param s_output input identifier
//	 * @return true, if identifier starts with 0_VALUE
//	 */
//	private boolean isLiteralOutput(String s_output) {
//		return s_output.toUpperCase().startsWith("0_VALUE");
//	}
//
//	/**
//	 * get literal process, used for chaining
//	 * @param s_output input literal identifier
//	 * @return literal output process
//	 */
//	private IOProcess getLiteral(String s_output) {
//		//value starts at index 8 (0_VALUE_*)
//		String value = s_output.substring(8);
//		//set formats
//		IOFormat defaultFormat = new IOFormat("", "", "xs:string");
//		Set<IOFormat> supportedFormats = getLiteralFormats(value);
//		//create node and process
//		IONode node = new IONode(null, "Literal", defaultFormat, supportedFormats, NodeType.OUTPUT);
//		Map<String,String> properties = new HashMap<String,String>();
//		properties.put("name", "Literal");
//		properties.put("value", decodeLiteralValue(value));
//		IOProcess process = new IOProcess("Literal", UUID.randomUUID().toString(), properties, node);
//		return process;
//	}
//
//	private String decodeLiteralValue(String value){
//		return value.trim().replace("_", ".");
//	}
//
//	/**
//	 * get possible literal formats for input string
//	 * @param literal input literal string
//	 * @return literal formats
//	 */
//	private Set<IOFormat> getLiteralFormats(String literal) {
//		Set<IOFormat> formats = new HashSet<IOFormat>();
//		formats.add(new IOFormat("", "", "xs:string"));
//		//identify supported formats
//		if(literal.matches("^(?i)(true|false)$"))
//			formats.add(new IOFormat("", "", "xs:boolean"));
//		if(literal.matches("^\\d+$"))
//			formats.add(new IOFormat("", "", "xs:integer"));
//		if(literal.matches("^(\\d+\\.?\\d*)|(\\d+\\_?\\d*)$"))	//must also check for "_", because "." is replaced in JS literal initiation
//			formats.add(new IOFormat("", "", "xs:double"));
//		return formats;
//	}
//
//	private IONode getNode(String processId, String ioId, Map<String, IOProcess> ioProcesses) throws IOException {
//		IOProcess process = getProcess(ioProcesses, processId);
//		if(process == null)
//			throw new IOException("no io process found for " + processId);
//		IONode node = getNode(process, ioId);
//		return node;
//	}
//
//	private IOProcess getProcess(Map<String, IOProcess> ioProcesses, String processId) {
//		return ioProcesses.get(processId);
//	}
//
//	private IONode getNode(IOProcess process, String ioId) {
//		return process.getNode(ioId);
//	}
//
//	public void validate() {
//		validationMessages = "";
//		//check connections
//		for(IOConnection connection : getConnections()){
//			if(!connection.isValid())
//				validationMessages += connection.getValidationMessage() + "<br/>";
//		}
//		//check process sequence
//		for(IOProcess process : ioProcesses.values()){
//			//continue, if process has either no ancestors or no successors
//			if(process.getAncestors(true).isEmpty() || process.getSuccessors(true).isEmpty())
//				continue;
//			//check, if ancestors and successors are disjoint (impossible sequence)
//			if(!Collections.disjoint(process.getAncestors(true), process.getSuccessors(true)))
//				validationMessages += "process " + process.getUUID() + " with no disjoint ancestors and successors (loop sequence)" + "<br/>";
//		}
//	}
//
//	/**
//	 * get all connections
//	 * @return all connections
//	 */
//	public Set<IOConnection> getConnections() { return connections; }
//
//	/**
//	 * get all connections for specified process
//	 * @param ioProcess input process
//	 * @return all connections for input process
//	 */
//	public Set<IOConnection> getConnections(IOProcess ioProcess) {
//		Set<IOConnection> processConnections = new HashSet<IOConnection>();
//		for(IOConnection connection : getConnections()){
//			if(ioProcess.participatesIn(connection))
//				processConnections.add(connection);
//		}
//		return processConnections;
//	}
//
//	public boolean isValid() { return validationMessages.isEmpty(); }
//	public String validationMessage() {	return validationMessages; }
//
//	/**
//	 * get processes ordered by execution sequence
//	 * @return processes ordered by execution sequence
//	 * @throws IOException
//	 */
//	public Deque<IOProcess> getProcessSequence() throws IOException {
//		Deque<IOProcess> deque = new ArrayDeque<IOProcess>();
//		short exitCode = 42;
//		//add processes to deque
//		while(exitCode == 42){
//			boolean changedDeque = false;
//			for(IOProcess process : ioProcesses.values()){
//				//continue if process is already in deque
//				if(deque.contains(process))
//					continue;
//				//continue if process has ancestors that are not yet in deque
//				if(!deque.containsAll(process.getAncestors(true)))
//					continue;
//				//add process to deque
//				deque.addLast(process);
//				changedDeque = true;
//			}
//			//set exit code 0, if deque contains all processes
//			if(deque.containsAll(ioProcesses.values())){
//				exitCode = 0;
//			}
//			//set exit code -1, if deque did not change
//			else if(!changedDeque)
//				exitCode = -1;
//		}
//		if(exitCode != 0)
//			throw new IOException("Processes cannot be put in sequence. Exit code: " + exitCode);
//		return deque;
//	}
//
//}
