//package de.tudresden.gis.fusion.client.ows;
//
//import de.tudresden.gis.fusion.client.ows.document.OWSCapabilities;
//import org.xml.sax.SAXException;
//
//import javax.faces.application.FacesMessage;
//import javax.faces.application.FacesMessage.Severity;
//import javax.faces.context.FacesContext;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
//public abstract class OWSHandler implements Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//	protected final String REQUEST_GETCAPABILITIES = "GetCapabilities";
//	protected final String PARAM_SERVICE = "service";
//	protected final String PARAM_VERSION = "version";
//	protected final String PARAM_REQUEST = "request";
//
//	private String sBaseURL;
//	public String getBaseURL() { return sBaseURL; }
//	public void setBaseURL(String sBaseURL) { this.sBaseURL = sBaseURL; }
//	public boolean validOWSBase() { return (this.getBaseURL() != null && this.getBaseURL().length() != 0); }
//
//	private Map<String,String> parameter = new HashMap<String,String>();
//	public String getParameter(String key) { return this.parameter.get(key.toLowerCase()); }
//	public void setParameter(String key, String value) { this.parameter.put(key.toLowerCase(), value); }
//	public void clearParameterList() { this.parameter.clear(); }
//
//	/**
//	 * get ows capabilities document from request
//	 * @return capabilites as DOM
//	 * @throws ParserConfigurationException
//	 * @throws SAXException
//	 * @throws IOException
//	 */
//	public OWSCapabilities getCapabilities() throws ParserConfigurationException, SAXException, IOException {
//		return new OWSCapabilities(getGetCapabilitiesRequest());
//	}
//
//	/**
//	 * get capabilities request
//	 * @return capabilities request
//	 * @throws IOException
//	 */
//	public String getGetCapabilitiesRequest() throws IOException {
//		this.setRequest(REQUEST_GETCAPABILITIES);
//		return getKVPRequest(new String[]{PARAM_SERVICE,PARAM_REQUEST}, new String[]{PARAM_VERSION});
//	}
//
//	/**
//	 * get KVP request
//	 * @param mandatoryKeys mandatory keys for the request
//	 * @param optionalKeys optional keys for the request
//	 * @throws IOException if base URL or a mandatory key is not set
//	 * @return
//	 */
//	public String getKVPRequest(String[] mandatoryKeys, String[] optionalKeys) throws IOException {
//
//		if(!this.validOWSBase())
//			throw new IOException("OWS base URL must not be null");
//
//		StringBuilder sBuilder = new StringBuilder().append(this.getBaseURL() + "?");
//		if(mandatoryKeys != null){
//			for(String key : mandatoryKeys) {
//				if(this.getParameter(key) == null || this.getParameter(key).length() == 0)
//					throw new IOException("KVP parameter " + key + " must not be null");
//				sBuilder.append(getKVPParameter(key, this.getParameter(key)) + "&");
//			}
//		}
//		if(optionalKeys != null){
//			for(String key : optionalKeys) {
//				if(this.getParameter(key) != null && this.getParameter(key).length() != 0)
//					sBuilder.append(getKVPParameter(key, this.getParameter(key)) + "&");
//			}
//		}
//
//		return sBuilder.substring(0, sBuilder.length()-1);
//
//	}
//
//	/**
//	 * get kvp parameter string
//	 * @param key parameter key
//	 * @param value parameter value
//	 * @return kvp parameter string
//	 */
//	public String getKVPParameter(String key, String value){
//		return key + "=" + value;
//	}
//
//	public String getService() { return this.getParameter(PARAM_SERVICE); }
//	public void setService(String value) { this.setParameter(PARAM_SERVICE, value); }
//
//	public String getRequest() { return this.getParameter(PARAM_REQUEST); }
//	public void setRequest(String value) { this.setParameter(PARAM_REQUEST, value); }
//
//	public String getVersion() { return this.getParameter(PARAM_VERSION); }
//	public void setVersion(String value) { this.setParameter(PARAM_VERSION, value); }
//
//	/**
//	 * append message to faces context
//	 * @param severity message severity level
//	 * @param summary message string
//	 * @param detail detailed message string
//	 */
//	protected void sendMessage(Severity severity, String summary, String detail){
//		FacesContext context = FacesContext.getCurrentInstance();
//		context.addMessage(null, new FacesMessage(severity, summary, detail) );
//	}
//
//}
