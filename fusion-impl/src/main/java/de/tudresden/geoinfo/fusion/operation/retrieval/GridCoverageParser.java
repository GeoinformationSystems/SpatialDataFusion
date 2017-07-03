package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class GridCoverageParser extends AbstractOperation {

    private static final String PROCESS_TITLE = GridCoverageParser.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for Grid Coverage formats";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "Coverage resource";

    private final static String OUT_COVERAGE_TITLE = "OUT_COVERAGE";
    private final static String OUT_COVERAGE_DESCRIPTION = "Parsed coverage";

    /**
     * constructor
     */
    public GridCoverageParser(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public @NotNull String getTitle() {
        return PROCESS_TITLE;
    }

    @Override
    public @NotNull String getDescription() {
        return PROCESS_DESCRIPTION;
    }

    @Override
    public void executeOperation() {
        //get input connectors
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        //get data
        URL resourceURL = ((URLLiteral) resourceConnector.getData()).resolve();
        //parse coverage
        GTGridFeature coverage;
        try {
            coverage = parseCoverage(resourceURL);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Could not parse coverage", e);
        }
        //set output connector
        connectOutput(OUT_COVERAGE_TITLE, coverage);
    }

    /**
     * parse coverage
     *
     * @param resourceURL coverage URL
     * @return coverage
     * @throws IOException if reading of the coverage fails
     */
    private GTGridFeature parseCoverage(URL resourceURL) throws IOException, URISyntaxException {
        File file;
        if(resourceURL.getProtocol().equalsIgnoreCase("file"))
            file = new File(resourceURL.toURI());
        else {
            file = File.createTempFile("coverage_" + UUID.randomUUID(), ".tmp");
            FileUtils.copyURLToFile(resourceURL, file);
        }
        return new GTGridFeature(new Identifier(resourceURL.toString()), file, null);
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_COVERAGE_TITLE, OUT_COVERAGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTGridFeature.class),
                        new MandatoryDataConstraint()},
                null);
    }

    /**
     * read shapefile
     *
     * @param url   grid URL
     * @return feature collection from shapefile
     */
    public static GTGridFeature readGrid(URL url) throws IOException, URISyntaxException {
        return new GridCoverageParser(null).parseCoverage(url);
    }

}
