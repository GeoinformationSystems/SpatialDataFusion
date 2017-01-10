package de.tudresden.gis.fusion.client.ows.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tudresden.gis.fusion.client.ows.document.desc.WPSIODescription;
import de.tudresden.gis.fusion.client.ows.orchestration.IONode;
import de.tudresden.gis.fusion.client.ows.orchestration.IONode.NodeType;

/**
 * wps process description
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WPSProcessDescription implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String identifier;
	private String title;
	private String description;
	private Map<String,WPSIODescription> inputs = new HashMap<String,WPSIODescription>();
	private Map<String,WPSIODescription> outputs = new HashMap<String,WPSIODescription>();
	
	private final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
	private final String PROCESS_TITLE = ".*(?i)Title";
	private final String PROCESS_ABSTRACT = ".*(?i)Abstract";
	private final String PROCESS_INPUTS = ".*(?i)DataInputs";
	private final String PROCESS_OUTPUTS = ".*(?i)ProcessOutputs";
	private final String PROCESS_INPUT = ".*(?i)Input";
	private final String PROCESS_OUTPUT = ".*(?i)Output";
	
	public WPSProcessDescription(Node descriptionNode) {
		//get child nodes
		NodeList layerNodes = descriptionNode.getChildNodes();
		//iterate
		for (int i=0; i<layerNodes.getLength(); i++) {
			//get element node
			Node element = layerNodes.item(i);
			//check for identifier
			if(element.getNodeName().matches(PROCESS_IDENTIFIER)){
				this.setIdentifier(element.getTextContent().trim());
			}
			//check for title
			if(element.getNodeName().matches(PROCESS_TITLE)){
				this.setTitle(element.getTextContent().trim());
			}
			//check for description
			if(element.getNodeName().matches(PROCESS_ABSTRACT)){
				this.setDescription(element.getTextContent().trim());
			}
			//check for inputs
			if(element.getNodeName().matches(PROCESS_INPUTS)){
				NodeList inputs = element.getChildNodes();
				for (int j=0; j<inputs.getLength(); j++) {
					Node inputElement = inputs.item(j);
					if(inputElement.getNodeName().matches(PROCESS_INPUT)){
						this.addInput(inputElement);
					}
				}	
			}
			//check for outputs
			if(element.getNodeName().matches(PROCESS_OUTPUTS)){
				NodeList inputs = element.getChildNodes();
				for (int j=0; j<inputs.getLength(); j++) {
					Node outputElement = inputs.item(j);
					if(outputElement.getNodeName().matches(PROCESS_OUTPUT)){
						this.addOutput(outputElement);
					}
				}
			}
		}
	}

	private void addInput(Node element) {
		WPSIODescription ioDesc = new WPSIODescription(element);
		inputs.put(ioDesc.getIdentifier(), ioDesc);
	}

	private void addOutput(Node element) {
		WPSIODescription ioDesc = new WPSIODescription(element);
		outputs.put(ioDesc.getIdentifier(), ioDesc);
	}
	
	//get JSON process description to be interpreted by jsPlumb
	public String getJSONDescription() {
		StringBuilder builder = new StringBuilder("{");
		builder.append("\"identifier\" : \"" + this.getIdentifier() + "\",");
		builder.append("\"uuid\" : \"" + getUUID() + "\",");
		builder.append("\"title\" : \"" + this.getTitle() + "\",");
		builder.append("\"description\" : \"" + this.getDescription() + "\",");
		builder.append("\"inputs\" : [");
		for(WPSIODescription desc : getInputs().values()){
			builder.append(desc.getJSONDescription() + ",");
		}
		builder.deleteCharAt(builder.length() - 1); //removes last comma
		builder.append("],");
		builder.append("\"outputs\" : [");
		for(WPSIODescription desc : getOutputs().values()){
			builder.append(desc.getJSONDescription() + ",");
		}
		builder.deleteCharAt(builder.length() - 1); //removes last comma
		builder.append("]");			
		builder.append("}");
		return builder.toString();
	}
	
	public Map<String,WPSIODescription> getInputs() { return inputs; }
	public Map<String,WPSIODescription> getOutputs() { return outputs; }

	public String getIdentifier() { return identifier; }
	public void setIdentifier(String identifier){ this.identifier = identifier; }
	
	public String getUUID() { return String.valueOf(this.hashCode()); }
	
	public String getTitle() { return title; }
	public void setTitle(String title){ this.title = title; }
	
	public String getDescription() { return description; }
	public void setDescription(String description){ this.description = description; }
	
	/**
	 * get process io nodes for chaining
	 * @return io nodes
	 */
	public Set<IONode> getIONodes() {
		Set<IONode> ioNodes = new HashSet<IONode>();
		for(WPSIODescription input : inputs.values()){
			ioNodes.add(new IONode(null, input.getIdentifier(), input.getDefaultFormat(), input.getSupportedFormats(), NodeType.INPUT));
		}
		for(WPSIODescription output : outputs.values()){
			ioNodes.add(new IONode(null, output.getIdentifier(), output.getDefaultFormat(), output.getSupportedFormats(), NodeType.OUTPUT));
		}
		return ioNodes;		
	}
	
}
