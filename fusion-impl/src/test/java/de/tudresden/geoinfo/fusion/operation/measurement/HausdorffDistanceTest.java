package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HausdorffDistanceTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_THRESHOLD = "IN_THRESHOLD";
    private final static String IN_BIDIRECTIONAL = "IN_BIDIRECTIONAL";
    private final static String IN_POINTS_ONLY = "IN_POINTS_ONLY";

    private final static String OUT_RUNTIME = "OUT_RUNTIME";
    private final static String OUT_MEASUREMENTS = "OUT_MEASUREMENTS";

    @Test
    public void getHausdorffDistance() throws IOException {
        calculateDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new DecimalLiteral(0.0005),
                null,
                null);
    }

    @Test
    public void getHausdorffDistance2() throws IOException {
        calculateDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new DecimalLiteral(0.0005),
                new BooleanLiteral(true),
                new BooleanLiteral(true));
    }

    private void calculateDistance(GTFeatureCollection domain, GTFeatureCollection range, DecimalLiteral threshold, BooleanLiteral bidirectional, BooleanLiteral pointsOnly) {

        AbstractOperation operation = new HausdorffDistance(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        inputs.put(IN_RANGE, range);
        inputs.put(IN_THRESHOLD, threshold);
        inputs.put(IN_BIDIRECTIONAL, bidirectional);
        inputs.put(IN_POINTS_ONLY, pointsOnly);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_MEASUREMENTS, RelationMeasurementCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
