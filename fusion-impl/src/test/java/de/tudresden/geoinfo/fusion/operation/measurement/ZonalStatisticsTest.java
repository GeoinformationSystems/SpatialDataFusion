package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ZonalStatisticsTest extends AbstractMeasurementTest {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_BAND = new Identifier("IN_BAND");
    private final static IIdentifier IN_BUFFER = new Identifier("IN_BUFFER");

    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");
    private final static IIdentifier OUT_MEASUREMENTS = new Identifier("OUT_MEASUREMENTS");

    @Test
    public void getZonalStatisticsGK() {
        calculateZonalStatistics(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "municipalities_gk.shp").toURI(), true),
                readGrid(new File("D:/Geodaten/Testdaten/tif/dem_gk.tif").toURI()),
                new IntegerLiteral(0),
                new DecimalLiteral(0.001)); //~100m in WGS84
    }

    @Test
    public void getZonalStatisticsWGS84() {
        calculateZonalStatistics(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "municipalities_wgs84.shp").toURI(), true),
                readGrid(new File("D:/Geodaten/Testdaten/tif/dem.tif").toURI()),
                new IntegerLiteral(0),
                new DecimalLiteral(0.001)); //~100m in WGS84
    }

    public void calculateZonalStatistics(GTFeatureCollection domain, GTGridFeature range, IntegerLiteral band, DecimalLiteral buffer) {

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_DOMAIN, domain);
        input.put(IN_RANGE, range);
        input.put(IN_BAND, band);
		input.put(IN_BUFFER, buffer);

        ZonalStatistics process = new ZonalStatistics();

        Map<IIdentifier,IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(OUT_MEASUREMENTS));
        Assert.assertTrue(output.get(OUT_MEASUREMENTS) instanceof RelationMeasurementCollection);

        RelationMeasurementCollection measurements = (RelationMeasurementCollection) output.get(OUT_MEASUREMENTS);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of reference features: " + domain.size() + "\n\t" +
                "number of measurements: " + measurements.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
