package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
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

public class BoundingBoxDistanceTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_THRESHOLD = "IN_THRESHOLD";

    private final static String OUT_RUNTIME = "OUT_RUNTIME";
    private final static String OUT_MEASUREMENTS = "OUT_MEASUREMENTS";

    @Test
    public void getBoundingBoxDistance() throws IOException {
        calculateDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                null);
    }

    @Test
    public void getBoundingBoxDistanceWithThreshold() throws IOException {
        calculateDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new DecimalLiteral(50d));
    }

    public void calculateDistance(GTFeatureCollection domain, GTFeatureCollection range, DecimalLiteral threshold) {

        AbstractOperation operation = new BoundingBoxOverlap(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        inputs.put(IN_RANGE, range);
        inputs.put(IN_THRESHOLD, threshold);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_MEASUREMENTS, RelationMeasurementCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
