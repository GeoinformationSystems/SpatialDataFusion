package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.GridCoverageParser;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ZonalStatisticsTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_BAND = "IN_BAND";
    private final static String IN_BUFFER = "IN_BUFFER";

    private final static String OUT_MEASUREMENTS = "OUT_MEASUREMENTS";

    @Test
    public void getZonalStatistics() throws IOException, URISyntaxException {
        calculateZonalStatistics(
                ShapefileParser.readShapefile(new File("src/test/resources/polygons.shp").toURI().toURL(), true),
                GridCoverageParser.readGrid(new File("src/test/resources/dem.tif").toURI().toURL()),
                new IntegerLiteral(0),
                new DecimalLiteral(1));
    }

    private void calculateZonalStatistics(GTFeatureCollection domain, GTGridFeature range, IntegerLiteral band, DecimalLiteral buffer) {

        AbstractOperation operation = new ZonalStatistics();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        inputs.put(IN_RANGE, range);
        inputs.put(IN_BAND, band);
        inputs.put(IN_BUFFER, buffer);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_MEASUREMENTS, RelationMeasurementCollection.class);

        this.execute(operation, inputs, outputs);

    }
}
