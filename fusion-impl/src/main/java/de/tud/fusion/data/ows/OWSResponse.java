package de.tud.fusion.data.ows;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tud.fusion.data.ResourceData;
import de.tud.fusion.data.description.IDataDescription;

/**
 * standard OWS response
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class OWSResponse extends ResourceData {
	
	/**
	 * constructor
	 * @param identifier OWS response identifier
	 * @param object OWS response document
	 * @param description OWS response description
	 */
	public OWSResponse(String identifier, Document object, IDataDescription description){
		super(identifier, object, description);
	}
	
	/**
	 * get OWS Document response
	 * @return response
	 */
	public Document resolve() {
		return (Document) super.resolve();
	}
	
	/**
	 * get first element from response with provided tag name
	 * @param regex regular expression for tag name
	 * @return
	 */
	public Node getNode(String regex){
		return getNode(regex, resolve().getChildNodes());
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
		return OWSResponse.getNodes(regex, resolve().getChildNodes(), null);
	}
	
	/**
	 * get nodes with specified tag name
	 * @param regex tag name as regex
	 * @param nodes input node list
	 * @param matches list with matches, initialized if null
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
				getNodes(regex, node.getChildNodes(), matches);
			}
		}
		return matches;
	}

}
