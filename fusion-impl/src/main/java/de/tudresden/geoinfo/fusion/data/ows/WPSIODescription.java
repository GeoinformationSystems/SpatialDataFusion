package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IO Metadata for WPS Process
 */
public class WPSIODescription {

    private final String IO_IDENTIFIER = ".*(?i)Identifier";
    private final String IO_TITLE = ".*(?i)Title";
    private final String IO_ABSTRACT = ".*(?i)Abstract";
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

    private IIdentifier identifier;
    private String title, abstrakt;
    private Set<WPSIOFormat> supportedFormats = new HashSet<>();
    private WPSIOFormat defaultFormat;
    private Set<IConnectionConstraint> formatConstraint;

    public WPSIODescription(Node ioNode) {
        //get child nodes
        NodeList ioNodes = ioNode.getChildNodes();
        //iterate
        for (int i = 0; i < ioNodes.getLength(); i++) {
            //get element node
            Node element = ioNodes.item(i);
            //check for identifier
            if (element.getNodeName().matches(IO_IDENTIFIER)) {
                this.setIdentifier(element.getTextContent().trim());
            }
            //check for title
            if (element.getNodeName().matches(IO_TITLE)) {
                this.setTitle(element.getTextContent().trim());
            }
            //check for abstract
            if (element.getNodeName().matches(IO_ABSTRACT)) {
                this.setAbstract(element.getTextContent().trim());
            }
        }

        //select all complex format elements
        List<Node> formatNodes = new ArrayList<>();
        OWSResponse.getNodes(IO_FORMAT, ioNode.getChildNodes(), formatNodes);
        //set formats
        for (Node formatNode : formatNodes) {
            NodeList nodes = formatNode.getChildNodes();
            WPSIOFormat format = new WPSIOFormat(null, null, null);
            for (int i = 0; i < nodes.getLength(); i++) {
                //get element node
                Node element = nodes.item(i);
                //check for mimetype
                if (element.getNodeName().matches(IO_MIMETYPE)) {
                    format.setMimetype(element.getTextContent().trim());
                }
                //check for schema
                if (element.getNodeName().matches(IO_SCHEMA)) {
                    format.setSchema(element.getTextContent().trim());
                }
            }
            //adds supported format; first format is set default
            this.addSupportedFormat(format);
            if (this.getDefaultFormat() == null)
                this.setDefaultFormat(format);
        }

        //select all literal data elements
        formatNodes.clear();
        OWSResponse.getNodes(IO_LITERAL, ioNode.getChildNodes(), formatNodes);
        //set formats
        for (Node formatNode : formatNodes) {
            NodeList nodes = formatNode.getChildNodes();
            WPSIOFormat format = new WPSIOFormat(null, null, null);
            for (int i = 0; i < nodes.getLength(); i++) {
                //get element node
                Node element = nodes.item(i);
                //check for data type
                if (element.getNodeName().matches(IO_LITERAL_TYPE)) {
                    NamedNodeMap atts = element.getAttributes();
                    for (int j = 0; j < atts.getLength(); j++) {
                        Node attribute = atts.item(j);
                        if (attribute.getNodeName().matches(IO_LITERAL_REF))
                            format.setType(attribute.getNodeValue().trim());
                    }
                }
            }
            //adds supported format; first format is set default
            this.addSupportedFormat(format);
            if (this.getDefaultFormat() == null)
                this.setDefaultFormat(format);
        }

        //select all bbox data elements
        formatNodes.clear();
        OWSResponse.getNodes(IO_BBOX, ioNode.getChildNodes(), formatNodes);
        //set formats
        for (Node formatNode : formatNodes) {
            NodeList nodes = formatNode.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                //get element node
                Node element = nodes.item(i);
                //check for data type
                if (element.getNodeName().matches(IO_BBOX_DEFAULT) || element.getNodeName().matches(IO_BBOX_SUPPORTED)) {
                    NodeList bboxNodes = element.getChildNodes();
                    WPSIOFormat format = new WPSIOFormat(null, null, null);
                    for (int j = 0; j < bboxNodes.getLength(); j++) {
                        Node bboxElement = bboxNodes.item(j);
                        if (bboxElement.getNodeName().matches(IO_BBOX_CRS)) {
                            format.setType(bboxElement.getTextContent().trim());
                        }
                    }
                    //adds supported format; first format is set default
                    this.addSupportedFormat(format);
                    if (this.getDefaultFormat() == null)
                        this.setDefaultFormat(format);
                }
            }
        }
    }

    public IIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = new Identifier(identifier);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return abstrakt;
    }

    public void setAbstract(String abstrakt) {
        this.abstrakt = abstrakt;
    }

    public Set<WPSIOFormat> getSupportedFormats() {
        return supportedFormats;
    }

    public boolean addSupportedFormat(WPSIOFormat supportedFormat) {
        return this.supportedFormats.add(supportedFormat);
    }

    public WPSIOFormat getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(WPSIOFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public boolean isSupported(WPSIOFormat format) {
        for (WPSIOFormat supportedFormat : supportedFormats) {
            if (supportedFormat.equals(format))
                return true;
        }
        return false;
    }

    public Set<IConnectionConstraint> getDescriptionConstraints() {
        if (formatConstraint == null) {
            formatConstraint = new HashSet<>();
            formatConstraint.add(new IOFormatConstraint(getSupportedFormats()));
        }
        return formatConstraint;
    }
}
