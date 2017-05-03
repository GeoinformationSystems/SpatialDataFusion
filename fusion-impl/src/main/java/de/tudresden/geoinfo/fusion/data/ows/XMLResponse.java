package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * standard OWS response
 */
public class XMLResponse extends Data<Document> {

    private URLLiteral uri;

    /**
     * constructor
     *
     * @param uri    OWS response uri literal
     * @param object OWS response document
     */
    public XMLResponse(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri.getIdentifier(), object, metadata);
        this.uri = uri;
    }

    /**
     * get nodes with specified tag name
     *
     * @param regex   tag name as regex
     * @param nodes   input node list
     * @param matches list with matches, initialized if null
     * @return list of nodes matching the regex
     */
    @NotNull
    public static List<Node> getNodes(@NotNull String regex, @NotNull NodeList nodes, @Nullable List<Node> matches) {
        if (matches == null)
            matches = new ArrayList<>();
        int i = 0;
        while (nodes.item(i) != null) {
            Node node = nodes.item(i++);
            if (node.getNodeName().matches(regex))
                matches.add(node);
            //continue anyway to address nested elements
            if (node.hasChildNodes()) {
                getNodes(regex, node.getChildNodes(), matches);
            }
        }
        return matches;
    }

    /**
     * get first element from document with provided tag name
     *
     * @param regex regular expression for tag name
     * @return first node matching the regex
     */
    public @Nullable Node getNode(@NotNull String regex) {
        return getNode(regex, resolve().getChildNodes());
    }

    /**
     * get first node with specified tag name
     *
     * @param regex tag name as regex
     * @param nodes input node list
     * @return first node matching the regex
     */
    public @Nullable Node getNode(@NotNull String regex, @NotNull NodeList nodes) {
        int i = 0;
        while (nodes.item(i) != null) {
            Node node = nodes.item(i++);
            if (node.getNodeName().matches(regex))
                return node;
            else if (node.hasChildNodes()) {
                Node tmpNode = this.getNode(regex, node.getChildNodes());
                if (tmpNode != null)
                    return tmpNode;
            }
        }
        return null;
    }

    /**
     * get all elements from response with provided tag name
     *
     * @param regex regular expression for tag name
     * @return list of nodes matching the regex
     */
    public @NotNull List<Node> getNodes(String regex) {
        return XMLResponse.getNodes(regex, resolve().getChildNodes(), null);
    }

    /**
     * get attribute value from set of attributes
     * @param regex input regex
     * @param attributes input attribute map
     * @return attribute value or null, if no attribute matched regex
     */
    public @Nullable String getAttributeValue(@NotNull String regex, NamedNodeMap attributes){
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().matches(regex))
                return attribute.getNodeValue().trim();
        }
        return null;
    }

    /**
     * get base URI of the response
     *
     * @return base URI literal
     */
    public @NotNull URLLiteral getURI() {
        return this.uri;
    }

}
