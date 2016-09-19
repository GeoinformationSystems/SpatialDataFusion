package de.tud.fusion.data.ows;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tud.fusion.data.description.IDataDescription;

/**
 * standard WPS capabilities
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WPSCapabilities extends OWSCapabilities {
	
	private final String WPS_PROCESS = ".*(?i)Process$";
	private final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
	
	private Set<String> wpsProcesses = new HashSet<String>();

	/**
	 * Constructor
	 * @param sRequest service request
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WPSCapabilities(String identifier, Document object, IDataDescription description) throws ParserConfigurationException, SAXException, IOException {
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
		this(owsCapabilities.getIdentifier(), owsCapabilities.resolve(), owsCapabilities.getDescription());
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
					if(identifier != null && !identifier.isEmpty())
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
