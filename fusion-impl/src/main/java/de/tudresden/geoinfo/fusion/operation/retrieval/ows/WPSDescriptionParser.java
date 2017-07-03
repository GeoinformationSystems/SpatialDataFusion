package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class WPSDescriptionParser extends OWSXMLParser {

    private static final String PROCESS_TITLE = WPSDescriptionParser.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for WPS ProcessDescription document";

    private static final String OUT_DESCRIPTION_TITLE = "OUT_DESCRIPTION";
    private static final String OUT_DESCRIPTION_DESCRIPTION = "Output WPS process description";

    /**
     * constructor
     */
    public WPSDescriptionParser(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        //parse document resource
        WPSDescribeProcess description;
        try {
            description = new WPSDescribeProcess(this.getResourceURI(), getDocument(), null);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Could not parse OWS XML resource", e);
        }
        //set output connector
        connectOutput(OUT_DESCRIPTION_TITLE, description);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_DESCRIPTION_TITLE, OUT_DESCRIPTION_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(WPSDescribeProcess.class),
                        new MandatoryDataConstraint()},
                null);
    }

    @NotNull
    @Override
    public String getTitle() {
        return PROCESS_TITLE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PROCESS_DESCRIPTION;
    }

}
