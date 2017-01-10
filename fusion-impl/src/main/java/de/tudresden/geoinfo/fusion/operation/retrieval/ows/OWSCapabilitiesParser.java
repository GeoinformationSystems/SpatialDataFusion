package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities.OWSServiceType;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.OutputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OWSCapabilitiesParser extends OWSXMLParser {

    private static final IIdentifier PROCESS = new Identifier(OWSCapabilitiesParser.class.getSimpleName());

	private final static IIdentifier OUT_CAPABILITIES = new Identifier("OUT_CAPABILITIES");

	/**
	 * constructor
	 */
	public OWSCapabilitiesParser() {
		super(PROCESS);
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
		connectOutput(OUT_CAPABILITIES, capabilities);
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
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_CAPABILITIES, new OutputConnector(
                OUT_CAPABILITIES,
                new MetadataForConnector(OUT_CAPABILITIES.toString(), "Output OWS capabilities"),
                new IDataConstraint[]{
                        new BindingConstraint(OWSCapabilities.class),
                        new MandatoryConstraint()},
                null));
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
