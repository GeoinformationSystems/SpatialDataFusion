package de.tudresden.gis.fusion.client.ows;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class OWSHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final String REQUEST_GETCAPABILITIES = "GetCapabilities";
	
	protected final String PARAM_SERVICE = "service";
	protected final String PARAM_VERSION = "version";
	protected final String PARAM_REQUEST = "request";
	
	private String sBaseURL;
	private Map<String,String> parameter = new HashMap<String,String>();
	
	/**
	 * get capabilities document
	 * @return capabilites as DOM
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document getCapabilities() throws ParserConfigurationException, SAXException, IOException {
		return getCapabilities(getGetCapabilitiesRequest());
	}
	
	/**
	 * get capabilities request
	 * @return capabilities request
	 * @throws IOException
	 */
	public String getGetCapabilitiesRequest() throws IOException {
		this.setRequest(REQUEST_GETCAPABILITIES);
		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST}, new String[]{PARAM_VERSION});
	}
	
	/**
	 * get capabilities document
	 * @param sRequest capabilities request
	 * @return capabilities document as DOM
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document getCapabilities(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		URL url = new URL(sRequest); 
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("Base URL is not valid or not accessible");
		//init capabilities document
		return getDocumentFromStream(connection.getInputStream());
	}
	
	/**
	 * parse capabilities document
	 * @param is XML stream
	 * @return capabilities document
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
	
	/**
	 * get KVP request
	 * @param mandatoryKeys mandatory keys for the request
	 * @param optionalKeys optional keys for the request
	 * @throws IOException if base URL or a mandatory key is not set
	 * @return
	 */
	protected String getKVPRequest(String[] mandatoryKeys, String[] optionalKeys) throws IOException {
		
		if(!this.validOWSBase())
			throw new IOException("OWS base URL must not be null");
		
		StringBuilder sBuilder = new StringBuilder().append(this.getBaseURL() + "?");
		if(mandatoryKeys != null){
			for(String key : mandatoryKeys) {
				if(this.getParameter(key) == null || this.getParameter(key).length() == 0)
					throw new IOException("KVP parameter " + key + " must not be null");
				sBuilder.append(getKVPParameter(key, this.getParameter(key)) + "&");
			}
		}
		if(optionalKeys != null){
			for(String key : optionalKeys) {
				if(this.getParameter(key) != null && this.getParameter(key).length() != 0)
					sBuilder.append(getKVPParameter(key, this.getParameter(key)) + "&");
			}
		}
		
		return sBuilder.substring(0, sBuilder.length()-1);
			
	}
	
	/**
	 * get kvp parameter string
	 * @param key parameter key
	 * @param value parameter value
	 * @return kvp parameter string
	 */
	public String getKVPParameter(String key, String value){
		return key + "=" + value;
	}
	
	public String getBaseURL() { return sBaseURL; }
	public void setBaseURL(String sBaseURL) { this.sBaseURL = sBaseURL; }
	
	public String getParameter(String key) { return this.parameter.get(key.toLowerCase()); }
	public void setParameter(String key, String value) { this.parameter.put(key.toLowerCase(), value); }
	
	public String getService() { return this.getParameter(PARAM_SERVICE); }
	public void setService(String value) { this.setParameter(PARAM_SERVICE, value); }
	
	public String getRequest() { return this.getParameter(PARAM_REQUEST); }
	public void setRequest(String value) { this.setParameter(PARAM_REQUEST, value); }
	
	public String getVersion() { return this.getParameter(PARAM_VERSION); }
	public void setVersion(String value) { this.setParameter(PARAM_VERSION, value); }
	
	public boolean validOWSBase() { return (this.getBaseURL() != null && this.getBaseURL().length() != 0); }
	
	/**
	 * get dom elements by regex
	 * @param nodes input nodes
	 * @param regex search regex
	 * @param matches output match list
	 * @return output matches
	 */
	protected List<Node> getElementsByRegEx(NodeList nodes, String regex, List<Node> matches){
		int i = 0;
		while(nodes.item(i) != null) {
			Node node = nodes.item(i++);
//			System.out.println(regex + " ; " + node.getNodeName() + " ; " + node.getNodeName().matches(regex));
			if(node.getNodeName().matches(regex))
				matches.add(node);
			else if(node.hasChildNodes()){
				this.getElementsByRegEx(node.getChildNodes(), regex, matches);
			}
		}
		return matches;
	}
	
	protected void sendMessage(Severity severity, String summary, String detail){
		//get faces message context
		FacesContext context = FacesContext.getCurrentInstance();
		//add message
		context.addMessage(null, new FacesMessage(severity, summary, detail) );		
	}
	
}
