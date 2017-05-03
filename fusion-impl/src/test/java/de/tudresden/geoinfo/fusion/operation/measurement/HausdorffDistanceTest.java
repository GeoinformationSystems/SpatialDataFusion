package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
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
    public void getHausdorffDistance() throws MalformedURLException {
        calculateDistance(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI().toURL(), true),
                new DecimalLiteral(50),
                null,
                null);
    }

    @Test
    public void getHausdorffDistance2() throws MalformedURLException {
        calculateDistance(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI().toURL(), true),
                new DecimalLiteral(50),
                new BooleanLiteral(true),
                new BooleanLiteral(true));
    }

    public void calculateDistance(GTFeatureCollection domain, GTFeatureCollection range, DecimalLiteral threshold, BooleanLiteral bidirectional, BooleanLiteral pointsOnly) {

        HausdorffDistance process = new HausdorffDistance();
        IIdentifier ID_IN_DOMAIN = process.getInputConnector(IN_DOMAIN).getIdentifier();
        IIdentifier ID_IN_RANGE = process.getInputConnector(IN_RANGE).getIdentifier();
        IIdentifier ID_IN_THRESHOLD = process.getInputConnector(IN_THRESHOLD).getIdentifier();
        IIdentifier ID_IN_BIDIRECTIONAL = process.getInputConnector(IN_BIDIRECTIONAL).getIdentifier();
        IIdentifier ID_IN_POINTS_ONLY = process.getInputConnector(IN_POINTS_ONLY).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();
        IIdentifier ID_OUT_MEASUREMENTS = process.getOutputConnector(OUT_MEASUREMENTS).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_DOMAIN, domain);
        input.put(ID_IN_RANGE, range);
        if (threshold != null)
            input.put(ID_IN_THRESHOLD, threshold);
        if (bidirectional != null)
            input.put(ID_IN_BIDIRECTIONAL, bidirectional);
        if (pointsOnly != null)
            input.put(ID_IN_POINTS_ONLY, pointsOnly);

        Map<IIdentifier, IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_MEASUREMENTS));
        Assert.assertTrue(output.get(ID_OUT_MEASUREMENTS) instanceof RelationMeasurementCollection);

        RelationMeasurementCollection measurements = (RelationMeasurementCollection) output.get(ID_OUT_MEASUREMENTS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of reference features: " + domain.size() + "\n\t" +
                "number of target features: " + range.size() + "\n\t" +
                "number of measurements: " + measurements.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
