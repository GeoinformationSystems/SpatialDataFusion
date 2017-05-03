package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class GridCoverageParser extends AbstractOperation {

    private static final String PROCESS_TITLE = GridCoverageParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for Grid Coverage formats";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "Coverage resource";

    private final static String OUT_COVERAGE_TITLE = "OUT_COVERAGE";
    private final static String OUT_COVERAGE_DESCRIPTION = "Parsed coverage";

    /**
     * constructor
     */
    public GridCoverageParser() {
        super(null, PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        //get data
        URL resourceURL = ((URLLiteral) resourceConnector.getData()).resolve();
        //parse coverage
        GTGridFeature coverage;
        try {
            coverage = parseCoverage(resourceURL);
        } catch (IOException e) {
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
    private GTGridFeature parseCoverage(URL resourceURL) throws IOException {
        InputStream stream;
        File tmpCoverage;
        tmpCoverage = File.createTempFile("coverage_" + UUID.randomUUID(), ".tmp");
        stream = resourceURL.openStream();
        FileOutputStream outputStream = new FileOutputStream(tmpCoverage);
        byte buf[] = new byte[4096];
        int len;
        while ((len = stream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        stream.close();
        return new GTGridFeature(new Identifier(resourceURL.toString()), tmpCoverage, null);
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_COVERAGE_TITLE, OUT_COVERAGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTGridFeature.class),
                        new MandatoryDataConstraint()},
                null);
    }

}
