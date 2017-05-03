package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
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

public class ZonalStatisticsTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_BAND = "IN_BAND";
    private final static String IN_BUFFER = "IN_BUFFER";

    private final static String OUT_RUNTIME = "OUT_RUNTIME";
    private final static String OUT_MEASUREMENTS = "OUT_MEASUREMENTS";

    @Test
    public void getZonalStatisticsGK() throws MalformedURLException {
        calculateZonalStatistics(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "municipalities_gk.shp").toURI(), true),
                readGrid(new File("D:/Geodaten/Testdaten/tif/dem_gk.tif").toURI().toURL()),
                new IntegerLiteral(0),
                new DecimalLiteral(100));
    }

    @Test
    public void getZonalStatisticsWGS84() throws MalformedURLException {
        calculateZonalStatistics(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "municipalities_wgs84.shp").toURI(), true),
                readGrid(new File("D:/Geodaten/Testdaten/tif/dem.tif").toURI().toURL()),
                new IntegerLiteral(0),
                null);
    }

    public void calculateZonalStatistics(GTFeatureCollection domain, GTGridFeature range, IntegerLiteral band, DecimalLiteral buffer) {

        ZonalStatistics process = new ZonalStatistics();
        IIdentifier ID_IN_DOMAIN = process.getInputConnector(IN_DOMAIN).getIdentifier();
        IIdentifier ID_IN_RANGE = process.getInputConnector(IN_RANGE).getIdentifier();
        IIdentifier ID_IN_BAND = process.getInputConnector(IN_BAND).getIdentifier();
        IIdentifier ID_IN_BUFFER = process.getInputConnector(IN_BUFFER).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();
        IIdentifier ID_OUT_MEASUREMENTS = process.getOutputConnector(OUT_MEASUREMENTS).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_DOMAIN, domain);
        input.put(ID_IN_RANGE, range);
        input.put(ID_IN_BAND, band);
        if (buffer != null)
            input.put(ID_IN_BUFFER, buffer);

        Map<IIdentifier, IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_MEASUREMENTS));
        Assert.assertTrue(output.get(ID_OUT_MEASUREMENTS) instanceof RelationMeasurementCollection);

        RelationMeasurementCollection measurements = (RelationMeasurementCollection) output.get(ID_OUT_MEASUREMENTS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of reference features: " + domain.size() + "\n\t" +
                "number of measurements: " + measurements.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
