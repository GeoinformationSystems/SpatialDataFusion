package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class OWSCapabilitiesParser extends OWSXMLParser {

    private static final String PROCESS_TITLE = OWSCapabilitiesParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for OWS OWSCapabilities document";

    private static final String SERVICE_WMS = "WMS";
    private static final String SERVICE_WFS = "WFS";
    private static final String SERVICE_WPS = "WPS";

    private static final String OUT_CAPABILITIES_TITLE = "OUT_CAPABILITIES";
    private static final String OUT_CAPABILITIES_DESCRIPTION = "Output WPS capabilities";

    /**
     * constructor
     */
    public OWSCapabilitiesParser() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }



    @Override
    public void execute() {
        //parse document resource
        OWSCapabilities capabilities;
        try {
            capabilities = new OWSCapabilities(this.getResourceURI(), getDocument(), null);
            if (capabilities.getServiceType().equals(SERVICE_WMS))
                capabilities = new WMSCapabilities(capabilities);
            else if (capabilities.getServiceType().equals(SERVICE_WFS))
                capabilities = new WFSCapabilities(capabilities);
            else if (capabilities.getServiceType().equals(SERVICE_WPS))
                capabilities = new WPSCapabilities(capabilities);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Could not parse OWS XML resource", e);
        }
        //set output connector
        connectOutput(OUT_CAPABILITIES_TITLE, capabilities);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_CAPABILITIES_TITLE, OUT_CAPABILITIES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(OWSCapabilities.class),
                        new MandatoryDataConstraint()},
                null);
    }

}
