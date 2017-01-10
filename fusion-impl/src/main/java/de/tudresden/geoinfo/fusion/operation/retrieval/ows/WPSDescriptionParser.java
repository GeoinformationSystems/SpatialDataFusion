package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescription;
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

public class WPSDescriptionParser extends OWSXMLParser {

    private static final IIdentifier PROCESS = new Identifier(WPSDescriptionParser.class.getSimpleName());

	private final static IIdentifier OUT_DESCRIPTION = new Identifier("OUT_DESCRIPTION");
	
	/**
	 * constructor
	 */
	public WPSDescriptionParser() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//parse document resource
		WPSProcessDescription description;
		try {
			description = new WPSProcessDescription(getDocumentIdentifier(), getDocument(), null);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException("Could not parse OWS XML resource", e);
		}
		//set output connector
		connectOutput(OUT_DESCRIPTION, description);
	}

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_DESCRIPTION, new OutputConnector(
                OUT_DESCRIPTION,
                new MetadataForConnector(OUT_DESCRIPTION.toString(), "Output WPS process description"),
                new IDataConstraint[]{
                        new BindingConstraint(WPSProcessDescription.class),
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
