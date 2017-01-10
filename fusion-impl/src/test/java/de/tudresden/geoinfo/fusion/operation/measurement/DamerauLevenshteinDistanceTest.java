package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DamerauLevenshteinDistanceTest extends AbstractMeasurementTest {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_DOMAIN_ATTRIBUTE = new Identifier("IN_DOMAIN_ATTRIBUTE");
    private final static IIdentifier IN_RANGE_ATTRIBUTE = new Identifier("IN_RANGE_ATTRIBUTE");
    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");
    private final static IIdentifier OUT_MEASUREMENTS = new Identifier("OUT_MEASUREMENTS");

    @Test
    public void getDamLevDistance() {
        getDamLevDistance(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                new StringLiteral("GN"),
                new StringLiteral("name"),
                null);
    }

    @Test
    public void getDamLevDistanceWithThreshold() {
        getDamLevDistance(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                new StringLiteral("GN"),
                new StringLiteral("name"),
                new IntegerLiteral(0));
    }

    public void getDamLevDistance(GTFeatureCollection domain, GTFeatureCollection range, StringLiteral sAttDomain, StringLiteral sAttRange, IntegerLiteral threshold) {

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_DOMAIN, domain);
        input.put(IN_RANGE, range);
        input.put(IN_DOMAIN_ATTRIBUTE, sAttDomain);
        input.put(IN_RANGE_ATTRIBUTE, sAttRange);
        if(threshold != null)
            input.put(IN_THRESHOLD, threshold);

        DamerauLevenshteinDistance process = new DamerauLevenshteinDistance();

        Map<IIdentifier,IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(OUT_MEASUREMENTS));
        Assert.assertTrue(output.get(OUT_MEASUREMENTS) instanceof RelationMeasurementCollection);

        RelationMeasurementCollection measurements = (RelationMeasurementCollection) output.get(OUT_MEASUREMENTS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of reference features: " + domain.size() + "\n\t" +
                "number of target features: " + range.size() + "\n\t" +
                "number of measurements: " + measurements.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}