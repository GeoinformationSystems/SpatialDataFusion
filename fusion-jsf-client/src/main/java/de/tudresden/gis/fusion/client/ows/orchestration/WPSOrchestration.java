package de.tudresden.gis.fusion.client.ows.orchestration;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.w3c.dom.Document;

public class WPSOrchestration implements Serializable {

	private static final long serialVersionUID = 1L;

	public WPSOrchestration(ConnectionHandler connectionHandler) throws IOException {
		
	}

	public void execute() throws IOException {
//		try {
//			if(getConnectionInvalid())
//				throw new IOException("Connections are invalid");
//
//			//get request
//			String request = getWPSRequest();
//			//execute
//			Document response = executeRequest(request);
//			
//			//validate response
//			if(isSuccess(response)){
//				setIsNotExecuted(false);
//				this.sendMessage(FacesMessage.SEVERITY_INFO, "Success",  "Process successfully executed");
//			}
//			else {
//				throw new IOException("Process failed");
//			}
//		} catch (IOException ioe) {
//			setIsNotExecuted(true);
//			this.sendMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not perform process: " + ioe.getLocalizedMessage());
//		}
	}

	private String getWPSRequest() {
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
