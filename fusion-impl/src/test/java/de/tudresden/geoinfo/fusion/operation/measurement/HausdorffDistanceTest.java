package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HausdorffDistanceTest extends AbstractMeasurementTest {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");
    private final static IIdentifier OUT_MEASUREMENTS = new Identifier("OUT_MEASUREMENTS");

    @Test
    public void getHausdorffDistance() {
        calculateDistance(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                new DecimalLiteral(50));
    }

    public void calculateDistance(GTFeatureCollection domain, GTFeatureCollection range, DecimalLiteral threshold) {

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_DOMAIN, domain);
        input.put(IN_RANGE, range);
        if(threshold != null)
            input.put(IN_THRESHOLD, threshold);

        HausdorffDistance process = new HausdorffDistance();

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
