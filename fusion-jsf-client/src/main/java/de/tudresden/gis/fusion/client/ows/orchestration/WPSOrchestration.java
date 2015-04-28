package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.IOException;
import java.io.Serializable;
import org.w3c.dom.Document;

public class WPSOrchestration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String WPS_HOST = "http://localhost";
	
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
	public void execute() throws IOException{
		//generate request
		String request = getWPSRequest(bpmnXML);
		//execute WPS and init response
		Document response = executeRequest(request);
		//evaluate response
		evaluateResponse(response);
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
	 * @return WPS response document
	 * @throws IOException
	 */
	private Document executeRequest(String request) throws IOException {

		System.out.println(request);
		
		return null;
	}

	/**
	 * evaluate WPS response message
	 * @param response response document
	 */
	private void evaluateResponse(Document response) {
		// TODO Auto-generated method stub
		
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
