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

@ManagedBean(name = "outputConnector")
@SessionScoped
public class OutputConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String ID = "0_Output";
	private final String TYPE = "Output";
	private final String NAME = "FusionOutput";
	
	/**
	 * get Relation storage as IOProcess for chaining purposes
	 * @return io process
	 */
	public IOProcess getIOProcess(){
		IOFormat defaultFormat = new IOFormat("text/xml", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd", "");
		IOFormat suppFormat1 = new IOFormat("application/json", "", "");
		IOFormat suppFormat2 = new IOFormat("text/turtle", "", "");
		Set<IOFormat> supportedFormats = new HashSet<IOFormat>();
		supportedFormats.add(defaultFormat);
		supportedFormats.add(suppFormat1);
		supportedFormats.add(suppFormat2);
		
		IONode node = new IONode(null, "IN_OUTPUT", defaultFormat, supportedFormats, NodeType.INPUT);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("name", NAME);
		IOProcess process = new IOProcess(TYPE, ID, properties, node);
		node.setProcess(process);
		return process;
	}

}
