package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.Data;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * standard OWS response
 */
public class OWSResponse extends Data<Document> {

    private URILiteral uri;

    /**
     * constructor
     *
     * @param uri    OWS response uri literal
     * @param object OWS response document
     */
    public OWSResponse(@NotNull URILiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
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
     * get first element from response with provided tag name
     *
     * @param regex regular expression for tag name
     * @return
     */
    public Node getNode(@NotNull String regex) {
        return getNode(regex, resolve().getChildNodes());
    }

    /**
     * get first node with specified tag name
     *
     * @param regex tag name as regex
     * @param nodes input node list
     * @return first node matching the regex
     */
    private Node getNode(@NotNull String regex, @NotNull NodeList nodes) {
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
     * @return
     */
    public List<Node> getNodes(String regex) {
        return OWSResponse.getNodes(regex, resolve().getChildNodes(), null);
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
