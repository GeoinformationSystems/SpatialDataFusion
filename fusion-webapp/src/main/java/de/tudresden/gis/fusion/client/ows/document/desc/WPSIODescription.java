package de.tudresden.gis.fusion.client.ows.document.desc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tudresden.gis.fusion.client.ows.document.OWSResponse;

/**
 * wps io description
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class WPSIODescription implements Serializable {
	
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
	
	public boolean isSupported(IOFormat format){
		for(IOFormat supportedFormat : supportedFormats){
			if(supportedFormat.equals(format))
				return true;
		}
		return false;
	}
	
}
