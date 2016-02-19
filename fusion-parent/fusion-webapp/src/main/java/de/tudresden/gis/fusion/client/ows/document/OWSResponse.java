package de.tudresden.gis.fusion.client.ows.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * basic OWS response
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OWSResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Document response;
	
	/**
	 * Constructor
	 * @param sRequest ows request
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public OWSResponse(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		this.response = getServerResponse(sRequest);
	}

	/**
	 * get OWS Document response
	 * @return response
	 */
	public Document getResponse() {
		return response;
	}
	
	/**
	 * get first element from response with provided tag name
	 * @param regex regular expression for tag name
	 * @return
	 */
	public Node getNode(String regex){
		return getNode(regex, getResponse().getChildNodes());
	}
	
	/**
	 * get first node with specified tag name
	 * @param regex tag name as regex
	 * @param nodes input node list
	 * @return first node matching the regex
	 */
	private Node getNode(String regex, NodeList nodes){
		int i = 0;
		while(nodes.item(i) != null) {
			Node node = nodes.item(i++);
			if(node.getNodeName().matches(regex))
				return node;
			else if(node.hasChildNodes()){
				Node tmpNode = this.getNode(regex, node.getChildNodes());
				if(tmpNode != null)
					return tmpNode;
			}
		}
		return null;
	}
	
	/**
	 * get all elements from response with provided tag name
	 * @param regex regular expression for tag name
	 * @return
	 */
	public List<Node> getNodes(String regex){
		return getNodes(regex, getResponse().getChildNodes(), null);
	}
	
	/**
	 * get nodes with specified tag name
	 * @param regex tag name as regex
	 * @param nodes input node list
	 * @param matches list with matches, will be initiated if null
	 * @return list of nodes matching the regex
	 */
	public static List<Node> getNodes(String regex, NodeList nodes, List<Node> matches){
		if(matches == null)
			matches = new ArrayList<Node>();
		int i = 0;
		while(nodes.item(i) != null) {
			Node node = nodes.item(i++);
			if(node.getNodeName().matches(regex))
				matches.add(node);
			else if(node.hasChildNodes()){
				OWSResponse.getNodes(regex, node.getChildNodes(), matches);
			}
		}
		return matches;
	}
	
	/**
	 * get OWS document
	 * @param sRequest capabilities request
	 * @return OWS document as DOM
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected Document getServerResponse(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		URL url = new URL(sRequest); 
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("Base URL is not valid or not accessible");
		//init capabilities document
		return getDocumentFromStream(connection.getInputStream());
	}
	
	/**
	 * parse OWS document
	 * @param is XML stream
	 * @return OWS document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document getDocumentFromStream(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		//parse document
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();
        //close
        is.close();
        //return document
        return doc;
	}

}
