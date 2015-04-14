package de.tudresden.gis.fusion.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.tudresden.gis.fusion.client.ows.document.desc.IOFormat;
import de.tudresden.gis.fusion.client.ows.orchestration.IONode;
import de.tudresden.gis.fusion.client.ows.orchestration.IOProcess;
import de.tudresden.gis.fusion.client.ows.orchestration.IONode.NodeType;

@ManagedBean(name = "fusekiConnector")
@SessionScoped
public class FusekiConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
//	private final String IN_RDF = "IN_RDF";
//	private final String IN_TRIPLE_STORE = "IN_TRIPLE_STORE";
//	private final String IN_CLEAR_STORE = "IN_CLEAR_STORE";	
//	private final String IN_URI_BASE = "IN_URI_BASE";
//	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
//	
//	private final String OUT_SUCCESS = "OUT_SUCCESS";
	
	private String fusekiURL;
	public String getFusekiURL() { return fusekiURL; }
	public void setFusekiURL(String fusekiURL) { this.fusekiURL = fusekiURL; }
	
	private boolean isEmpty = true;
	public boolean getIsEmpty() { return isEmpty; }
	public void setIsEmpty(boolean isEmpty) { this.isEmpty = isEmpty; }
	
	/**
	 * get Relation storage as IOProcess for chaining purposes
	 * @return io process
	 */
	public IOProcess getIOProcess(){
		IOFormat defaultFormat = new IOFormat("text/turtle", "", "");
		Set<IOFormat> supportedFormats = new HashSet<IOFormat>();
		supportedFormats.add(defaultFormat);
		IONode node = new IONode(null, "Relations", defaultFormat, supportedFormats, NodeType.INPUT);
		IOProcess process = new IOProcess(this.getFusekiURL(), "SPARQLEndpoint", node);
		node.setProcess(process);
		return process;
	}

}
