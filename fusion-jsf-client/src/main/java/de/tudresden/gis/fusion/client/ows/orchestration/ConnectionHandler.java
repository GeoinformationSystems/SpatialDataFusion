package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class ConnectionHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<ProcessConnection> connections;

	public ConnectionHandler(String sConnections) throws IllegalArgumentException {
		connections = new HashSet<ProcessConnection>();
		try {
			JSONArray jConnections = new JSONArray(sConnections);
			for(int i=0; i<jConnections.length(); i++){
				ProcessConnection connection = new ProcessConnection(jConnections.getJSONObject(i));
				connections.add(connection);
			}
			validate();
		} catch (JSONException e) {
			throw new IllegalArgumentException("Connection description is no valid JSON");
		}
	}
	
	private void validate() {
		// TODO Auto-generated method stub
		
	}

	public boolean isValid() {
		return false;
	}
	
	public String validationMessage() {	
		return "Number of connections: " + connections.size();
	}
	
//	private String getRelationOperations(WPSHandler wpsConnection) throws JSONException {
//		//get connections as JSON object
//		JSONArray connections = wpsConnection.decodeConnections().getJSONArray(CONNECTION_COLLECTION);
//		Map<String,String> orderedOperationIdentifier = new LinkedHashMap<String,String>();
//		for(int i=0; i<connections.length(); i++){
//			JSONObject connection = connections.getJSONObject(i);
//			String ref_id = connection.getJSONObject(CONNECTION_REF_DESC).getString(CONNECTION_ID);
//			String tar_id = connection.getJSONObject(CONNECTION_TAR_DESC).getString(CONNECTION_ID);			
//			String ref_out = connection.getString(CONNECTION_REF_OUTPUT);
//			String tar_in = connection.getString(CONNECTION_TAR_INPUT);
//		}
//		
//		
//		// TODO Auto-generated method stub
//		return null;
//	}

	private class ProcessConnection implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private final String CONNECTION_REF_DESC = "ref_description";
		private final String CONNECTION_REF_OUTPUT = "ref_output";
		private final String CONNECTION_TAR_DESC = "tar_description";
		private final String CONNECTION_TAR_INPUT = "tar_input";
		private final String CONNECTION_ID = "identifier";
		
		public ProcessConnection(JSONObject jsonObject){
			
		}
		
	}
	
}
