package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.w3c.dom.Document;

import de.tudresden.gis.fusion.client.ows.WFSHandler;
import de.tudresden.gis.fusion.client.ows.WPSHandler;

public class WPSOrchestration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String AGGREGATE_SERVICE_URL = "http://localhost:8080/52n-wps-webapp/WebProcessingService";
	private final String AGGREGATE_SERVICE_NAME = "de.tudresden.gis.fusion.algorithm.RelationAggregate";
	private final String AGGREGATE_SERVICE_IN_TARGET = "IN_TARGET";
	private final String AGGREGATE_SERVICE_IN_REFERENCE = "IN_REFERENCE";
	private final String AGGREGATE_SERVICE_IN_OPERATIONS = "IN_OPERATIONS";
	
	private final String CONNECTION_COLLECTION = "connections";
	private final String CONNECTION_REF_DESC = "ref_description";
	private final String CONNECTION_REF_OUTPUT = "ref_output";
	private final String CONNECTION_TAR_DESC = "tar_description";
	private final String CONNECTION_TAR_INPUT = "tar_input";
	private final String CONNECTION_ID = "identifier";
	
	private boolean isNotExecuted = true;
	public boolean getIsNotExecuted() { return isNotExecuted; }

	public void executeProcess(WFSHandler referenceWFSHandler, WFSHandler targetWFSHandler, WPSHandler wpsConnection) throws IOException, JSONException {
		this.isNotExecuted = true;
		//get reference WFS url
		String referenceWFS = referenceWFSHandler.getBaseURL();
		//get target WFS url
		String targetWFS = targetWFSHandler.getBaseURL();
		//get connections as required by aggregation service
		String operations = getRelationOperations(wpsConnection);
		//get request
		String request = getWPSRequest(referenceWFS, targetWFS, operations);
		//execute
		Document response = executeRequest(request);
		//validate response
		if(!isSuccess(response))
			throw new IOException();
		else
			this.isNotExecuted = false;
	}
	
	private String getRelationOperations(WPSHandler wpsConnection) throws JSONException {
		//get connections as JSON object
		JSONArray connections = wpsConnection.decodeConnections().getJSONArray(CONNECTION_COLLECTION);
		Map<String,String> orderedOperationIdentifier = new LinkedHashMap<String,String>();
		for(int i=0; i<connections.length(); i++){
			JSONObject connection = connections.getJSONObject(i);
			String ref_id = connection.getJSONObject(CONNECTION_REF_DESC).getString(CONNECTION_ID);
			String tar_id = connection.getJSONObject(CONNECTION_TAR_DESC).getString(CONNECTION_ID);			
			String ref_out = connection.getString(CONNECTION_REF_OUTPUT);
			String tar_in = connection.getString(CONNECTION_TAR_INPUT);
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

	private String getWPSRequest(String referenceWFS, String targetWFS,
			String operations) {
		// TODO Auto-generated method stub
		return null;
	}

	private Document executeRequest(String request) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isSuccess(Document response) {
		// TODO Auto-generated method stub
		return false;
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
	

//	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
//    <wps:Execute service="WPS" version="1.0.0"
//    xmlns:wps="http://www.opengis.net/wps/1.0.0"
//    xmlns:ows="http://www.opengis.net/ows/1.1"
//    xmlns:ogc="http://www.opengis.net/ogc"
//    xmlns:xlink="http://www.w3.org/1999/xlink"
//    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//    xsi:schemaLocation="http://www.opengis.net/wps/1.0.0
//    http://schemas.opengis.net/wps/1.0.0/wpsExecute_request.xsd">
//    <ows:Identifier>de.tudresden.gis.fusion.algorithm.OperationAggregate</ows:Identifier>
//       <wps:DataInputs>
//         <wps:Input>
//           <ows:Identifier>IN_REFERENCE</ows:Identifier>
//           <wps:Reference
//    schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd"
//    xlink:href="http://localhost:8081/geoserver/fusion/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=fusion:atkis_dd"
//    method="GET"/>
//         </wps:Input>
//         <wps:Input>
//           <ows:Identifier>IN_TARGET</ows:Identifier>
//           <wps:Reference
//    schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd"
//    xlink:href="http://localhost:8081/geoserver/fusion/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=fusion:osm_dd"
//    method="GET"/>
//         </wps:Input>
//         <wps:Input>
//           <ows:Identifier>IN_OPERATIONS</ows:Identifier>
//           <wps:Data>
//             <wps:LiteralData dataType="xs:string">BoundingBoxDistance,IN_THRESHOLD,LITERAL,50,IN_DROP_RELATIONS,LITERAL,true;LengthDifference,IN_THRESHOLD,LITERAL,30,IN_DROP_RELATIONS,LITERAL,false,IN_RELATIONS,AngleDifference,OUT_RELATIONS;AngleDifference,IN_THRESHOLD,LITERAL,0.063,IN_DROP_RELATIONS,LITERAL,true,IN_RELATIONS,BoundingBoxDistance,OUT_RELATIONS;</wps:LiteralData>
//           </wps:Data>
//         </wps:Input>
//       </wps:DataInputs>
//       <wps:ResponseForm>
//             <wps:RawDataOutput>
//             <ows:Identifier>OUT_OUTPUT</ows:Identifier>
//           </wps:RawDataOutput>
//       </wps:ResponseForm>
//    </wps:Execute>
}
