package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class WPSOrchestration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String WPS_HOST = "http://localhost:8080/52n-wps-webapp/WebProcessingService";
	private final String SUCCESS_MSG = "Process successful";
	
	private String bpmnXML;
	private boolean success = false;
	private String responseMessage;

	/**
	 * constructor for BPMN orchestration
	 * @param bpmnXML input BPMN XML model
	 */
	public WPSOrchestration(String bpmnXML) {
		this.bpmnXML = bpmnXML;		
	}
	
	/**
	 * execute orchestration
	 * @throws IOException 
	 */
	public boolean execute() throws IOException{
		//generate request
		String request = getWPSRequest(bpmnXML);
		//execute WPS and init response
		return executeRequest(request);
	}

	/**
	 * create WPS request
	 * @param bpmnXML input BPMN model
	 * @return WPS request
	 */
	private String getWPSRequest(String bpmnXML) {
		return new WPSRequestBuilder(bpmnXML).buildRequest();
	}

	/**
	 * execute request
	 * @param request WPS request
	 * @return true if process has been successfully executed
	 * @throws IOException
	 */
	private boolean executeRequest(String request) throws IOException {
		//init connection
		URL url = new URL(WPS_HOST);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Length", String.valueOf(request.getBytes().length));
		//send request
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(request);
		out.flush();out.close();
		//get response
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		//evaluate
		return evaluateResponse(response.toString());
	}

	/**
	 * evaluate WPS response message
	 * @param response response document
	 */
	private boolean evaluateResponse(String response) {
		return response.toLowerCase().contains(SUCCESS_MSG.toLowerCase());
	}
	
	/**
	 * check, if WPS response indicates successful execution
	 * @return true, if process response indicates success
	 */
	public boolean isSuccess() {
		return success;
	}
	
	/**
	 * return WPS response message
	 * @return response message
	 */
	public String getResponseMessage() {
		return responseMessage;
	}
}
