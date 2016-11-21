package de.tud.fusion.operation.retrieval.ows;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import de.tud.fusion.data.ows.WPSProcessDescription;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.OutputConnector;

public class WPSDescriptionParser extends OWSXMLParser {
	
	public final static String PROCESS_ID = WPSDescriptionParser.class.getSimpleName();
		
	private final String OUT_DESCRIPTION = "OUT_DESCRIPTION";
	
	private Set<IOutputConnector> outputConnectors;
	
	/**
	 * constructor
	 */
	public WPSDescriptionParser() {
		super(PROCESS_ID);
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
		setOutputConnector(OUT_DESCRIPTION, description);
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_DESCRIPTION, OUT_DESCRIPTION, "Output WPS process description",
				new HashSet<IDataConstraint>(Arrays.asList(new IDataConstraint[]{
						new BindingConstraint(WPSProcessDescription.class),
						new MandatoryConstraint()})),
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
