package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescriptions;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class WPSDescriptionParser extends OWSXMLParser {

    private static final String PROCESS_TITLE = WPSDescriptionParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for WPS ProcessDescription document";

    private static final String OUT_DESCRIPTION_TITLE = "OUT_DESCRIPTION";
    private static final String OUT_DESCRIPTION_DESCRIPTION = "Output WPS process description";

    /**
     * constructor
     */
    public WPSDescriptionParser() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //parse document resource
        WPSProcessDescriptions description;
        try {
            description = new WPSProcessDescriptions(this.getResourceURI(), getDocument(), null);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Could not parse OWS XML resource", e);
        }
        //set output connector
        connectOutput(OUT_DESCRIPTION_TITLE, description);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_DESCRIPTION_TITLE, OUT_DESCRIPTION_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(WPSProcessDescriptions.class),
                        new MandatoryConstraint()},
                null);
    }

}
