package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * standard WPS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WPSCapabilities extends OWSCapabilities {
	
	private static final String WPS_PROCESS = ".*(?i)Process$";
	private static final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
	
	private Set<String> wpsProcesses = new HashSet<>();

	/**
	 * Constructor
	 * @param identifier service request
	 * @param object capabilities document
	 * @param description document description
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WPSCapabilities(IIdentifier identifier, Document object, IMetadataForData description) throws ParserConfigurationException, SAXException, IOException {
		super(identifier, object, description);
		if(!this.getServiceType().equals(OWSServiceType.WPS))
			throw new IOException("Response is not a valid WPS capabilities document");
		initWPSCapabilities();
	}
	
	/**
	 * Constructor
	 * @param owsCapabilities input capabilities
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public WPSCapabilities(OWSCapabilities owsCapabilities) throws ParserConfigurationException, SAXException, IOException {
		this(owsCapabilities.getIdentifier(), owsCapabilities.resolve(), owsCapabilities.getMetadata());
	}

	/**
	 * initialize WPS processes
	 */
	private void initWPSCapabilities() {
		List<Node> matches = this.getNodes(WPS_PROCESS);
		for(Node processNode : matches) {
			NodeList processNodes = processNode.getChildNodes();
			//search for identifier
			String identifier = null;
			for (int i=0; i<processNodes.getLength(); i++) {
				Node element = processNodes.item(i);
				if(element.getNodeName().matches(PROCESS_IDENTIFIER)){
					identifier = element.getTextContent().trim();
					if(!identifier.isEmpty())
						wpsProcesses.add(identifier);
				}
			}
		}
	}
	
	/**
	 * get WPS process names
	 * @return process names
	 */
	public Set<String> getWPSProcesses(){
		return wpsProcesses;
	}

}
