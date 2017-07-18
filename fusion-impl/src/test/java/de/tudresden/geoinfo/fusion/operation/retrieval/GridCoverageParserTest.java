package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class GridCoverageParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_COVERAGE = "OUT_COVERAGE";

    @Test
    public void readCoverage() throws MalformedURLException {
        readCoverage(new URLLiteral(new File("src/test/resources/dem.tif").toURI().toURL()));
    }

    private void readCoverage(URLLiteral resource) {

        AbstractOperation operation = new GridCoverageParser();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_COVERAGE, GTGridFeature.class);

        this.execute(operation, inputs, outputs);

    }

}
