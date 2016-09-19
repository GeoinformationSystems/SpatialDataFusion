package de.tud.fusion.operation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.data.ows.WPSCapabilities;
import de.tud.fusion.data.ows.WPSProcessDescription;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.retrieval.ows.OWSCapabilitiesParser;
import de.tud.fusion.operation.retrieval.ows.WPSDescriptionParser;

public class WPSProxyOperation extends AbstractOperation {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String OUT_CAPABILITIES = "OUT_CAPABILITIES";
	private final String OUT_DESCRIPTION = "OUT_DESCRIPTION";
	
	private final String SERVICE = "WPS";
	private final String VERSION = "1.0.0";
	private final String REQUEST_GC = "GetCapabilities";
	private final String REQUEST_DP = "DescribeProcess";
	private final String REQUEST_E = "Execute";
	
	private String endpoint, identifier;
	private WPSCapabilities capabilities;
	private WPSProcessDescription description;

	public WPSProxyOperation(String endpoint, String identifier) {
		super(identifier);
		this.endpoint = endpoint;
		this.identifier = identifier;
		initProcess();
	}

	private void initProcess() {
		initCapabilities();
		initDescription();
	}
	
	private void initCapabilities() {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put(IN_RESOURCE, getCapabilitiesRequest());
		OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
		Map<String,IData> output = parser.execute(input);
		if(!output.containsKey(OUT_CAPABILITIES) || !(output.get(OUT_CAPABILITIES) instanceof WPSCapabilities))
			throw new RuntimeException("An error occurred while reading capabilities from " + this.endpoint);
		capabilities = (WPSCapabilities) output.get(OUT_CAPABILITIES);
		if(!capabilities.getWPSProcesses().contains(this.identifier))
			throw new RuntimeException("WPS at " + this.endpoint + " does not provide the requested process " + this.identifier);
	}

	private URILiteral getCapabilitiesRequest() {
		return new URILiteral(URI.create(this.endpoint + "?service=" + SERVICE + "&version=" + VERSION + "&request=" + REQUEST_GC));
	}

	private void initDescription() {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put(IN_RESOURCE, getDescribeProcessRequest());
		WPSDescriptionParser parser = new WPSDescriptionParser();
		Map<String,IData> output = parser.execute(input);
		if(!output.containsKey(OUT_DESCRIPTION) || !(output.get(OUT_DESCRIPTION) instanceof WPSProcessDescription))
			throw new RuntimeException("An error occurred while reading description from " + this.endpoint);
		description = (WPSProcessDescription) output.get(OUT_DESCRIPTION);
	}
	
	private URILiteral getDescribeProcessRequest() {
		return new URILiteral(URI.create(this.endpoint + "?service=" + SERVICE + "&version=" + VERSION + "&request=" + REQUEST_DP + "&identifier=" + this.identifier));
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<IInputConnector> getInputConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessAbstract() {
		// TODO Auto-generated method stub
		return null;
	}

}
