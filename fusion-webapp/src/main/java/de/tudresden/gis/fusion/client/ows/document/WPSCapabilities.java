//package de.tudresden.gis.fusion.client.ows.document;
//
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//public class WPSCapabilities extends OWSCapabilities {
//
//	private static final long serialVersionUID = 1L;
//
//	private final String WPS_PROCESS = ".*(?i)Process$";
//	private final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
//
//	private Set<String> wpsProcesses = new HashSet<String>();
//
//	/**
//	 * Constructor
//	 * @param sRequest service request
//	 * @throws ParserConfigurationException
//	 * @throws SAXException
//	 * @throws IOException
//	 */
//	public WPSCapabilities(String sRequest) throws ParserConfigurationException, SAXException, IOException {
//		super(sRequest);
//		if(!this.getServiceType().equals(OWSServiceType.WPS))
//			throw new IOException("Response is not a WPS capabilities document");
//		parseWPSProcesses();
//	}
//
//	/**
//	 * parse WPS processes
//	 */
//	private void parseWPSProcesses() {
//		List<Node> matches = this.getNodes(WPS_PROCESS);
//		for(Node processNode : matches) {
//			NodeList processNodes = processNode.getChildNodes();
//			//search for identifier
//			String identifier = null;
//			for (int i=0; i<processNodes.getLength(); i++) {
//				Node element = processNodes.item(i);
//				if(element.getNodeName().matches(PROCESS_IDENTIFIER)){
//					identifier = element.getTextContent().trim();
//					if(identifier != null && !identifier.isEmpty())
//						wpsProcesses.add(identifier);
//				}
//			}
//		}
//	}
//
//	/**
//	 * get WPS process names
//	 * @return process names
//	 */
//	public Set<String> getWPSProcesses(){
//		return wpsProcesses;
//	}
//
//}
