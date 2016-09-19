package de.tud.fusion.operation.retrieval.ows;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import de.tud.fusion.data.ows.OWSCapabilities;
import de.tud.fusion.data.ows.OWSCapabilities.OWSServiceType;
import de.tud.fusion.data.ows.WFSCapabilities;
import de.tud.fusion.data.ows.WMSCapabilities;
import de.tud.fusion.data.ows.WPSCapabilities;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class OWSCapabilitiesParser extends OWSXMLParser {
	
	public final static String PROCESS_ID = OWSCapabilitiesParser.class.getSimpleName();
		
	private final String OUT_CAPABILITIES = "OUT_CAPABILITIES";
	
	private Set<IOutputConnector> outputConnectors;
	
	/**
	 * constructor
	 */
	public OWSCapabilitiesParser() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//parse document resource
		OWSCapabilities capabilities;
		try {
			capabilities = getCapabilities(new OWSCapabilities(getDocumentIdentifier(), getDocument(), null));
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException("Could not parse OWS XML resource", e);
		}
		//set output connector
		setOutputConnector(OUT_CAPABILITIES, capabilities);
	}
	
	private OWSCapabilities getCapabilities(OWSCapabilities owsCapabilities) throws ParserConfigurationException, SAXException, IOException {
		if(owsCapabilities.getServiceType().equals(OWSServiceType.WMS))
			return new WMSCapabilities(owsCapabilities);
		if(owsCapabilities.getServiceType().equals(OWSServiceType.WFS))
			return new WFSCapabilities(owsCapabilities);
		if(owsCapabilities.getServiceType().equals(OWSServiceType.WPS))
			return new WPSCapabilities(owsCapabilities);
		else
			throw new IllegalArgumentException("OWS Service Type is not supported");
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_CAPABILITIES, OUT_CAPABILITIES, "Output OWS capabilities",
				new IDataConstraint[]{
						new BindingConstraint(OWSCapabilities.class, false),
						new MandatoryConstraint()},
				null));		
		//return
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "OWS Capabilities Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for OWS Capabilities document";
	}

}
