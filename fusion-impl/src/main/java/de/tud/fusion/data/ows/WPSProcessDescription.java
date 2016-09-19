package de.tud.fusion.data.ows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tud.fusion.data.description.IDataDescription;
import de.tud.fusion.data.description.IOFormat;
import de.tud.fusion.operation.constraint.IOFormatConstraint;
import de.tud.fusion.operation.description.IDescriptionConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.OutputConnector;
import de.tud.fusion.operation.description.InputConnector;

/**
 * standard WPS process description
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WPSProcessDescription extends OWSResponse {
	
	private final String PROCESS_DESCRIPTION = ".*(?i)ProcessDescription$";
	
	private Map<String,WPSProcess> wpsProcesses;

	/**
	 * Constructor
	 * @param identifier document identifier
	 * @param object WPS description document
	 * @param description WPS document description
	 */
	public WPSProcessDescription(String identifier, Document object, IDataDescription description) {
		super(identifier, object, description);
		initWPSProcessDescription();
	}
	
	/**
	 * parse WPS process description
	 */
	private void initWPSProcessDescription() {
		List<Node> matches = this.getNodes(PROCESS_DESCRIPTION);
		wpsProcesses = new HashMap<String,WPSProcess>();
		for(Node description : matches) {
			WPSProcess process = new WPSProcess(description);
			if(process != null)
				wpsProcesses.put(process.getIdentifier(), process);
		}
	}
	
	/**
	 * get WPS process identifier
	 * @return process description identifier
	 */
	public Set<String> getProcessIdentifier() {
		return wpsProcesses.keySet();
	}
	
	/**
	 * get WPS process description by identifier
	 * @param identifier description identifier
	 * @return WPS process description with specified identifier
	 */
	public WPSProcess getProcessDescription(String identifier) {
		return this.wpsProcesses.get(identifier);
	}

	/**
	 * WPS process description
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	public static class WPSProcess {
		
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
		
		public WPSProcess(Node ioNode) {
			//get child nodes
			NodeList layerNodes = ioNode.getChildNodes();
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
		 * get input connector
		 * @param identifier input identifier
		 * @return input connector for specified identifier
		 */
		public IInputConnector getInputConnector(String identifier){
			if(!inputs.containsKey(identifier))
				throw new IllegalArgumentException("WPS has no input identifier " + identifier);
			WPSIODescription wpsIO = inputs.get(identifier);
			return new InputConnector(wpsIO.getIdentifier(), wpsIO.getTitle(), null, null, wpsIO.getDescriptionConstraints(), null);
				
		}
		
		/**
		 * get output connector
		 * @param identifier output identifier
		 * @return output connector for specified identifier
		 */
		public IOutputConnector getOutputConnector(String identifier){
			if(!outputs.containsKey(identifier))
				throw new IllegalArgumentException("WPS has no output identifier " + identifier);
			WPSIODescription wpsIO = outputs.get(identifier);
			return new OutputConnector(wpsIO.getIdentifier(), wpsIO.getTitle(), null, null, wpsIO.getDescriptionConstraints());
		}
		
	}
	
	/**
	 * IO Description for WPS Process
	 * @author Stefan Wiemann, TU Dresden
	 *
	 */
	public static class WPSIODescription {
		
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
		private Set<IDescriptionConstraint> formatConstraint;
		
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
		
		public boolean isSupported(IOFormat format){
			for(IOFormat supportedFormat : supportedFormats){
				if(supportedFormat.equals(format))
					return true;
			}
			return false;
		}
		
		public Set<IDescriptionConstraint> getDescriptionConstraints(){
			if(formatConstraint == null){
				formatConstraint = new HashSet<IDescriptionConstraint>();
				formatConstraint.add(new IOFormatConstraint(getSupportedFormats()));
			}
			return formatConstraint;
		}
	}
	
}
