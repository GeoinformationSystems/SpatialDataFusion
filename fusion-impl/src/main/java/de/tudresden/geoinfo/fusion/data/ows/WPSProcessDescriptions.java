package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * standard WPS process description
 */
public class WPSProcessDescriptions extends OWSResponse {

    private static final String PROCESS_DESCRIPTION = ".*(?i)ProcessDescription$";

    private Map<String, WPSProcessDescription> wpsProcesses;

    /**
     * Constructor
     *
     * @param uri    WPS description uri
     * @param object WPS description document
     */
    public WPSProcessDescriptions(@NotNull URILiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initWPSProcessDescription();
    }

    /**
     * parse WPS process description
     */
    private void initWPSProcessDescription() {
        List<Node> matches = this.getNodes(PROCESS_DESCRIPTION);
        wpsProcesses = new HashMap<>();
        for (Node description : matches) {
            WPSProcessDescription process = new WPSProcessDescription(this.getURI(), description);
            wpsProcesses.put(process.getIdentifier(), process);
        }
    }

    /**
     * get WPS process identifier
     *
     * @return process description identifier
     */
    @NotNull
    public Set<String> getProcessIdentifier() {
        return wpsProcesses.keySet();
    }

    /**
     * get WPS process description by identifier
     *
     * @param identifier description identifier
     * @return WPS process description with specified identifier
     */
    @NotNull
    public WPSProcessDescription getProcessDescription(@NotNull String identifier) {
        return this.wpsProcesses.get(identifier);
    }

    /**
     * get WPS process description
     *
     * @return Single WPS process description
     */
    @NotNull
    public WPSProcessDescription getProcessDescription() throws UnsupportedOperationException {
        if (this.wpsProcesses.size() != 1)
            throw new UnsupportedOperationException("Operation is only valid for single process descriptions");
        return this.wpsProcesses.values().iterator().next();
    }

}
