package de.tudresden.gis.fusion.client.ows.document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WPSProcessDescriptions extends OWSResponse {
	
	private static final long serialVersionUID = 1L;
	
	private final String PROCESS_DESCRIPTION = ".*(?i)ProcessDescription$";
	
	private Map<String,ProcessDescription> wpsProcesses;

	/**
	 * Constructor
	 * @param sRequest service request
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WPSProcessDescriptions(String sRequest) throws ParserConfigurationException, SAXException, IOException {
		super(sRequest);
		wpsProcesses = new HashMap<String,ProcessDescription>();
		parseDescriptions();
	}

	/**
	 * parse WPS process description
	 */
	private void parseDescriptions() {
		List<Node> matches = this.getNodes(PROCESS_DESCRIPTION);
		for(Node description : matches) {
			ProcessDescription process = new ProcessDescription(description);
			if(process != null)
				wpsProcesses.put(process.getIdentifier(), process);
		}
	}
	
	/**
	 * get process description as JSON, used for jsPlumb
	 * @param process input process
	 * @return JSON process descriptions
	 */
	public String getJSONDescription(String process) {
		if(process == null || !wpsProcesses.containsKey(process))
			return null;
		return wpsProcesses.get(process).getJSONDescription();
	}
	
	/**
	 * get process descriptions as JSON, used for jsPlumb
	 * @return JSON process descriptions
	 */
	public String getJSONDescription(Set<String> processes) {
		if(processes == null || processes.isEmpty())
			return null;
		
		StringBuilder builder = new StringBuilder("{ \"descriptions\" : [");
		for(String process : processes){
			if(wpsProcesses.containsKey(process))
				builder.append(wpsProcesses.get(process).getJSONDescription() + ",");
		}
		builder.deleteCharAt(builder.length() - 1); //removes last comma
		builder.append("] }");
		return builder.toString();
	}
	
	/**
	 * get process names
	 * @return process names
	 */
	public Set<String> getProcessIdentifier() {
		return wpsProcesses.keySet();
	}
	
	/**
	 * get process description
	 * @param name process name
	 * @return process description
	 */
	public String getDescription(String name) {
		if(wpsProcesses.get(name) != null)
			return wpsProcesses.get(name).getDescription();
		return null;
	}
	
	/**
	 * wps process description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class ProcessDescription implements Serializable {
		
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
		
		public ProcessDescription(Node descriptionNode) {
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
		
		public String getTitle() { return title; }
		public void setTitle(String title){ this.title = title; }
		
		public String getDescription() { return description; }
		public void setDescription(String description){ this.description = description; }	
	}
	
	/**
	 * wps io description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class WPSIODescription implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private final String IO_IDENTIFIER = ".*(?i)Identifier";
		private final String IO_TITLE = ".*(?i)Title";
		private final String IO_FORMAT = ".*(?i)Format";
		private final String IO_MIMETYPE = ".*(?i)MimeType";		
		private final String IO_SCHEMA = ".*(?i)Schema";
		private final String IO_LITERAL = ".*(?i)LiteralData|.*(?i)LiteralOutput";
		private final String IO_LITERAL_TYPE = ".*(?i)DataType";
		private final String IO_LITERAL_REF = ".*(?i)reference";
		private final String IO_BBOX = ".*(?i)BoundingBoxData|.*(?i)BoundingBoxOutput";
		private final String IO_BBOX_DEFAULT = ".*(?i)Default";
		private final String IO_BBOX_SUPPORTED = ".*(?i)Supported";
		private final String IO_BBOX_CRS = ".*(?i)CRS";
		
		private String identifier;
		private String title;
		private Set<IOFormat> supportedFormats = new TreeSet<IOFormat>();
		private IOFormat defaultFormat;
		
		public WPSIODescription(Node ioNode) {
			//get child nodes
			NodeList ioNodes = ioNode.getChildNodes();
			//iterate
			for (int i=0; i<ioNodes.getLength(); i++) {
				//get element node
				Node element = ioNodes.item(i);
				//check for identifier
				if(element.getNodeName().matches(IO_IDENTIFIER)){
					this.setIdentifier(element.getTextContent().trim());
				}
				//check for title
				if(element.getNodeName().matches(IO_TITLE)){
					this.setTitle(element.getTextContent().trim());
				}
			}
			
			//select all complex format elements
			List<Node> formatNodes = new ArrayList<Node>();
			OWSResponse.getNodes(IO_FORMAT, ioNode.getChildNodes(), formatNodes);
			//set formats
			for(Node formatNode : formatNodes){
				NodeList nodes = formatNode.getChildNodes();
				IOFormat format = new IOFormat();
				for (int i=0; i<nodes.getLength(); i++) {
					//get element node
					Node element = nodes.item(i);
					//check for mimetype
					if(element.getNodeName().matches(IO_MIMETYPE)){
						format.setMimetype(element.getTextContent().trim());
					}
					//check for schema
					if(element.getNodeName().matches(IO_SCHEMA)){
						format.setSchema(element.getTextContent().trim());
					}
				}
				//adds supported format; first format is set default
				this.addSupportedFormat(format);
				if(this.getDefaultFormat() == null)
					this.setDefaultFormat(format);
			}
			
			//select all literal data elements
			formatNodes.clear();
			OWSResponse.getNodes(IO_LITERAL, ioNode.getChildNodes(), formatNodes);
			//set formats
			for(Node formatNode : formatNodes){
				NodeList nodes = formatNode.getChildNodes();
				IOFormat format = new IOFormat();
				for (int i=0; i<nodes.getLength(); i++) {
					//get element node
					Node element = nodes.item(i);
					//check for data type
					if(element.getNodeName().matches(IO_LITERAL_TYPE)){
						NamedNodeMap atts = element.getAttributes();
						for (int j=0; j<atts.getLength(); j++) {
							Node attribute = atts.item(j);
							if(attribute.getNodeName().matches(IO_LITERAL_REF))
								format.setType(attribute.getNodeValue().trim());
						}
					}
				}
				//adds supported format; first format is set default
				this.addSupportedFormat(format);
				if(this.getDefaultFormat() == null)
					this.setDefaultFormat(format);
			}
			
			//select all bbox data elements
			formatNodes.clear();
			OWSResponse.getNodes(IO_BBOX, ioNode.getChildNodes(), formatNodes);
			//set formats
			for(Node formatNode : formatNodes){
				NodeList nodes = formatNode.getChildNodes();
				for (int i=0; i<nodes.getLength(); i++) {
					//get element node
					Node element = nodes.item(i);
					//check for data type
					if(element.getNodeName().matches(IO_BBOX_DEFAULT) || element.getNodeName().matches(IO_BBOX_SUPPORTED)){
						NodeList bboxNodes = element.getChildNodes();
						IOFormat format = new IOFormat();
						for (int j=0; j<bboxNodes.getLength(); j++) {
							Node bboxElement = bboxNodes.item(j);
							if(bboxElement.getNodeName().matches(IO_BBOX_CRS)){
								format.setType(bboxElement.getTextContent().trim());
							}
						}
						//adds supported format; first format is set default
						this.addSupportedFormat(format);
						if(this.getDefaultFormat() == null)
							this.setDefaultFormat(format);
					}
				}
			}
		}

		public String getIdentifier() { return identifier; }
		public void setIdentifier(String identifier){ this.identifier = identifier; }
		
		public String getTitle() { return title; }
		public void setTitle(String title){ this.title = title; }
		
		public Set<IOFormat> getSupportedFormats() { return supportedFormats; }
		public boolean addSupportedFormat(IOFormat supportedFormat){ return this.supportedFormats.add(supportedFormat); }
		
		public IOFormat getDefaultFormat() { return defaultFormat; }
		public void setDefaultFormat(IOFormat defaultFormat){ this.defaultFormat = defaultFormat; }
		
		//get JSON process description to be interpreted by jsPlumb
		public String getJSONDescription() {
			StringBuilder builder = new StringBuilder("{");
			builder.append("\"identifier\" : \"" + this.getIdentifier() + "\",");
			builder.append("\"title\" : \"" + this.getTitle() + "\",");
			builder.append(getDefaultFormat() != null ? "\"defaultFormat\" : " + this.getDefaultFormat().getJSONDescription() + "," : "");
			builder.append("\"supportedFormats\" : [");
			for(IOFormat desc : getSupportedFormats()){
				builder.append(desc.getJSONDescription() + ",");
			}
			if(builder.charAt(builder.length() - 1) == ',')
				builder.deleteCharAt(builder.length() - 1); //removes last comma
			builder.append("]");			
			builder.append("}");
			return builder.toString();
		}
		
	}
	
	/**
	 * wps io format
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	private class IOFormat implements Serializable,Comparable<IOFormat> {
		
		private static final long serialVersionUID = 1L;
		
		private String mimetype = "";
		private String schema = "";
		private String type = "";
		
		public String getMimetype() { return mimetype; }
		public void setMimetype(String mimetype) { this.mimetype = mimetype; }
		
		public String getSchema() { return schema; }
		public void setSchema(String schema) { this.schema = schema; }
		
		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		
		/**
		 * compares formats based on mimetype and schema
		 */
		public boolean equals(Object obj){
			if(obj instanceof IOFormat &&
					((IOFormat) obj).getMimetype().equalsIgnoreCase(getMimetype()) && 
					((IOFormat) obj).getSchema().equalsIgnoreCase(getSchema()) &&
					((IOFormat) obj).getType().equalsIgnoreCase(getType()))
				return true;
			return false;
		}
		
		//get JSON process description to be interpreted by jsPlumb
		public String getJSONDescription() {
			StringBuilder builder = new StringBuilder("{");
			builder.append(getMimetype() != "" ? "\"mimetype\" : \"" + this.getMimetype() + "\"," : "");
			builder.append(getSchema() != "" ? "\"schema\" : \"" + this.getSchema() + "\"," : "");	
			builder.append(getType() != "" ? "\"type\" : \"" + this.getType() + "\"," : "");
			if(builder.charAt(builder.length() - 1) == ',')
				builder.deleteCharAt(builder.length() - 1); //removes last comma
			builder.append("}");
			return builder.toString();
		}
		
		public int compareTo(IOFormat format) {
			if(this.equals(format))
				return 0;
			else
				return 1;
		}
		
	}

}
