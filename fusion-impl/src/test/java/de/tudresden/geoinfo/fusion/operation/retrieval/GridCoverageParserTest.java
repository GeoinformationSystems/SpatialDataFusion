package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GridCoverageParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_COVERAGE = "OUT_COVERAGE";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readGeoTIFF() {
        readCoverage(new URILiteral(new File("D:/Geodaten/Testdaten/tif/dem.tif").toURI()));
    }

    private void readCoverage(URILiteral resource) {

        GridCoverageParser parser = new GridCoverageParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_COVERAGE = parser.getOutputConnector(OUT_COVERAGE).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, resource);

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_COVERAGE));
        Assert.assertTrue(output.get(ID_OUT_COVERAGE) instanceof GTGridFeature);

        GTGridFeature grid = (GTGridFeature) output.get(ID_OUT_COVERAGE);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "bounds: " + grid.getRepresentation().getBounds() + "\n\t" +
                "feature crs: " + (grid.resolve().getCoordinateReferenceSystem() != null ? grid.resolve().getCoordinateReferenceSystem().getName() : "not set") + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
