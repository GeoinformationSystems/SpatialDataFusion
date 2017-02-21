package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * WPS process description
 */
public class WPSProcessDescription {

    private final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
    private final String PROCESS_TITLE = ".*(?i)Title";
    private final String PROCESS_ABSTRACT = ".*(?i)Abstract";
    private final String PROCESS_INPUTS = ".*(?i)DataInputs";
    private final String PROCESS_OUTPUTS = ".*(?i)ProcessOutputs";
    private final String PROCESS_INPUT = ".*(?i)Input";
    private final String PROCESS_OUTPUT = ".*(?i)Output";
    private String identifier;
    private String title;
    private String description;
    private URILiteral uri;
    private Map<IIdentifier, WPSIODescription> inputs = new HashMap<>();
    private Map<IIdentifier, WPSIODescription> outputs = new HashMap<>();

    public WPSProcessDescription(URILiteral uri, Node ioNode) {
        this.uri = uri;
        //get child nodes
        NodeList layerNodes = ioNode.getChildNodes();
        //iterate
        for (int i = 0; i < layerNodes.getLength(); i++) {
            //get element node
            Node element = layerNodes.item(i);
            //check for identifier
            if (element.getNodeName().matches(PROCESS_IDENTIFIER)) {
                this.setIdentifier(element.getTextContent().trim());
            }
            //check for title
            if (element.getNodeName().matches(PROCESS_TITLE)) {
                this.setTitle(element.getTextContent().trim());
            }
            //check for description
            if (element.getNodeName().matches(PROCESS_ABSTRACT)) {
                this.setDescription(element.getTextContent().trim());
            }
            //check for inputs
            if (element.getNodeName().matches(PROCESS_INPUTS)) {
                NodeList inputs = element.getChildNodes();
                for (int j = 0; j < inputs.getLength(); j++) {
                    Node inputElement = inputs.item(j);
                    if (inputElement.getNodeName().matches(PROCESS_INPUT)) {
                        this.addInput(inputElement);
                    }
                }
            }
            //check for outputs
            if (element.getNodeName().matches(PROCESS_OUTPUTS)) {
                NodeList inputs = element.getChildNodes();
                for (int j = 0; j < inputs.getLength(); j++) {
                    Node outputElement = inputs.item(j);
                    if (outputElement.getNodeName().matches(PROCESS_OUTPUT)) {
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

    public Map<IIdentifier, WPSIODescription> getInputs() {
        return inputs;
    }

    public Map<IIdentifier, WPSIODescription> getOutputs() {
        return outputs;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get base URI of the response
     *
     * @return base URI literal
     */
    public URILiteral getURI() {
        return this.uri;
    }

}
