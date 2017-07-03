package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DamerauLevenshteinDistanceTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_DOMAIN_ATTRIBUTE = "IN_DOMAIN_ATTRIBUTE";
    private final static String IN_RANGE_ATTRIBUTE = "IN_RANGE_ATTRIBUTE";
    private final static String IN_THRESHOLD = "IN_THRESHOLD";

    private final static String OUT_RUNTIME = "OUT_RUNTIME";
    private final static String OUT_MEASUREMENTS = "OUT_MEASUREMENTS";

    @Test
    public void getDamLevDistance() throws IOException {
        getDamLevDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new StringLiteral("name"),
                new StringLiteral("GN"),
                null);
    }

    @Test
    public void getDamLevDistanceWithThreshold0() throws IOException {
        getDamLevDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new StringLiteral("name"),
                new StringLiteral("GN"),
                new IntegerLiteral(0));
    }

    @Test
    public void getDamLevDistanceWithThreshold10() throws IOException {
        getDamLevDistance(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new StringLiteral("name"),
                new StringLiteral("GN"),
                new IntegerLiteral(10));
    }

    private void getDamLevDistance(GTFeatureCollection domain, GTFeatureCollection range, StringLiteral sAttDomain, StringLiteral sAttRange, IntegerLiteral threshold) {

        AbstractOperation operation = new DamerauLevenshteinDistance(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        inputs.put(IN_RANGE, range);
        inputs.put(IN_DOMAIN_ATTRIBUTE, sAttDomain);
        inputs.put(IN_RANGE_ATTRIBUTE, sAttRange);
        inputs.put(IN_THRESHOLD, threshold);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_MEASUREMENTS, RelationMeasurementCollection.class);

        this.execute(operation, inputs, outputs);

    }

}