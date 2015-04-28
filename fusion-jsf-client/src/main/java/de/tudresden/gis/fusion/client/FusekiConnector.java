package de.tudresden.gis.fusion.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	
	private final String ID = "0_OutputRelations";
	private final String TYPE = "SPARQLEndpoint";
	private final String NAME = "FusekiSPARQLEndpoint";
	
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
		IONode node = new IONode(null, "IN_RDF", defaultFormat, supportedFormats, NodeType.INPUT);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("NAME", NAME);
		properties.put("IN_TRIPLE_STORE", fusekiURL);
		IOProcess process = new IOProcess(TYPE, ID, properties, node);
		node.setProcess(process);
		return process;
	}

}
