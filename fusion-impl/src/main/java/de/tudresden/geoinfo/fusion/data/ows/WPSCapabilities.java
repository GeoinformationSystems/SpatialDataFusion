package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * standard WPS capabilities
 */
public class WPSCapabilities extends OWSCapabilities {

    private static final String WPS_PROCESS = ".*(?i)Process$";
    private static final String PROCESS_IDENTIFIER = ".*(?i)Identifier";
    private static final String SERVICE_WPS = "(?i)WPS";

    private Set<String> wpsProcesses = new HashSet<>();

    /**
     * Constructor
     *
     * @param uri    WPS capabilities uri
     * @param object capabilities document
     */
    public WPSCapabilities(@NotNull URLLiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initWPSCapabilities();
    }

    /**
     * Constructor
     *
     * @param capabilities input capabilities
     */
    public WPSCapabilities(@NotNull OWSCapabilities capabilities) {
        super(capabilities.getURI(), capabilities.resolve(), capabilities.getMetadata(), capabilities.getServiceIdentification(), capabilities.getOperationsMetadata());
        initWPSCapabilities();
    }

    /**
     * initialize WPS processes
     */
    private void initWPSCapabilities() {
        if (!this.getServiceType().matches(SERVICE_WPS))
            throw new IllegalArgumentException("Document is not a valid WPS capabilities document");
        List<Node> matches = this.getNodes(WPS_PROCESS);
        for (Node processNode : matches) {
            NodeList processNodes = processNode.getChildNodes();
            //search for identifier
            String identifier;
            for (int i = 0; i < processNodes.getLength(); i++) {
                Node element = processNodes.item(i);
                if (element.getNodeName().matches(PROCESS_IDENTIFIER)) {
                    identifier = element.getTextContent().trim();
                    if (!identifier.isEmpty())
                        wpsProcesses.add(identifier);
                }
            }
        }
    }

    /**
     * get WPS process names
     *
     * @return process names
     */
    @NotNull
    public Set<String> getWPSProcesses() {
        return wpsProcesses;
    }

}
