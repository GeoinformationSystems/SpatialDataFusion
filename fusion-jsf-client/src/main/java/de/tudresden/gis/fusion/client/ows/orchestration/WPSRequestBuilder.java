package de.tudresden.gis.fusion.client.ows.orchestration;

public class WPSRequestBuilder {
	
	//TODO make configurable
	private final String WPS_IDENTIFIER = "de.tudresden.gis.fusion.algorithm.BPMNAggregate";
	private final String IN_BPMN = "IN_BPMN";
	private final String OUT_RESULT = "OUT_RESULT";
	
	private String bpmnXML;
	
	public WPSRequestBuilder(String bpmnXML) {
		this.setBpmnXML(bpmnXML);
	}

	public String getBpmnXML() { return bpmnXML; }
	public void setBpmnXML(String bpmnXML) { this.bpmnXML = bpmnXML; }
	
	public String buildRequest(){
		return getRequest(this.bpmnXML);
	}
	
	private String getRequest(String bpmnXML){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
				"<wps:Execute service=\"WPS\" version=\"1.0.0\"\n" +
				"xmlns:wps=\"http:www.opengis.net/wps/1.0.0\"\n" +
				"xmlns:ows=\"http:www.opengis.net/ows/1.1\"\n" +
				"xmlns:ogc=\"http:www.opengis.net/ogc\"\n" +
				"xmlns:xlink=\"http:www.w3.org/1999/xlink\"\n" +
				"xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"http:www.opengis.net/wps/1.0.0 http:schemas.opengis.net/wps/1.0.0/wpsExecute_request.xsd\">\n" +
				"<ows:Identifier>" + WPS_IDENTIFIER + "</ows:Identifier>\n" +
					"<wps:DataInputs>\n" +
				    	"<wps:Input>\n" +
				    		"<ows:Identifier>" + IN_BPMN + "</ows:Identifier>\n" +
			    			"<wps:ComplexData schema=\"http://www.omg.org/spec/BPMN/20100524/MODEL\">\n" +
			    				prepareBPMN(bpmnXML) +
			    			"</wps:ComplexData>\n" +
				    	"</wps:Input>\n" +
				    "</wps:DataInputs>\n" +
				    "<wps:ResponseForm>\n" +
				   		"<wps:ResponseDocument storeExecuteResponse=\"false\" lineage=\"false\" status=\"false\">\n" +
					   		"<wps:Output asReference=\"false\" mimeType=\"text/xml\" encoding=\"UTF-8\">\n" +
					   			"<ows:Identifier>" + OUT_RESULT + "</ows:Identifier>\n" +
					   		"</wps:Output>\n" +
				       "</wps:ResponseDocument>\n" +
				   "</wps:ResponseForm>\n" +
			   "</wps:Execute>";
	}

	private String prepareBPMN(String bpmnXML) {
		return bpmnXML.substring(bpmnXML.indexOf("?>") + 2).trim();
	}
}