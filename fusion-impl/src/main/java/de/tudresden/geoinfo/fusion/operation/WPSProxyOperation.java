package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescription;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.OWSCapabilitiesParser;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.WPSDescriptionParser;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WPSProxyOperation extends AbstractOperation {
	
	private static final IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
	private static final IIdentifier OUT_CAPABILITIES = new Identifier("OUT_CAPABILITIES");
	private static final IIdentifier OUT_DESCRIPTION = new Identifier("OUT_DESCRIPTION");
	
	private static final String SERVICE = "WPS";
	private static final String VERSION = "1.0.0";
	private static final String REQUEST_GC = "GetCapabilities";
	private static final String REQUEST_DP = "DescribeProcess";
	private static final String REQUEST_E = "Execute";
	
	private String endpoint, identifier;
	private WPSCapabilities capabilities;
	private WPSProcessDescription description;

	public WPSProxyOperation(String endpoint, String identifier) {
		super(new Identifier(identifier));
		this.endpoint = endpoint;
		this.identifier = identifier;
		initProcess();
	}

	private void initProcess() {
		initCapabilities();
		initDescription();
	}
	
	private void initCapabilities() {
		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, getCapabilitiesRequest());
		OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
		Map<IIdentifier,IData> output = parser.execute(input);
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
		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, getDescribeProcessRequest());
		WPSDescriptionParser parser = new WPSDescriptionParser();
		Map<IIdentifier,IData> output = parser.execute(input);
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
	public Map<IIdentifier,IInputConnector> initInputConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
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
