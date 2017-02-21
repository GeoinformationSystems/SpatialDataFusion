package de.tudresden.geoinfo.fusion.operation.harmonization;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CRSReprojectTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_CRS = "IN_CRS";

    private final static String OUT_DOMAIN = "OUT_DOMAIN";
    private final static String OUT_RANGE = "OUT_RANGE";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void reprojectWithRangeCRS() {
        reproject(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_svs_wgs84.shp").toURI(), true),
                null);
    }

    @Test
    public void reprojectWithCustomCRS() {
        reproject(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                null,
                new URILiteral(URI.create("http://www.opengis.net/def/crs/EPSG/0/4326")));
    }

    @Test
    public void reprojectWithCustomCRSAndRange() {
        reproject(
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI(), true),
                readShapefile(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI(), true),
                new URILiteral(URI.create("http://www.opengis.net/def/crs/EPSG/0/4326")));
    }

    public void reproject(GTFeatureCollection domain, GTFeatureCollection range, URILiteral crsURI) {

        CRSReproject process = new CRSReproject();
        IIdentifier ID_IN_DOMAIN = process.getInputConnector(IN_DOMAIN).getIdentifier();
        IIdentifier ID_IN_RANGE = process.getInputConnector(IN_RANGE).getIdentifier();
        IIdentifier ID_IN_CRS = process.getInputConnector(IN_CRS).getIdentifier();
        IIdentifier ID_OUT_RANGE = process.getOutputConnector(OUT_RANGE).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();
        IIdentifier ID_OUT_DOMAIN = process.getOutputConnector(OUT_DOMAIN).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_DOMAIN, domain);
        if (range != null)
            input.put(ID_IN_RANGE, range);
        if (crsURI != null)
            input.put(ID_IN_CRS, crsURI);

        Map<IIdentifier, IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_DOMAIN));
        Assert.assertTrue(output.get(ID_OUT_DOMAIN) instanceof GTFeatureCollection);

        if (range != null)
            Assert.assertTrue(output.get(ID_OUT_RANGE) instanceof GTFeatureCollection);

        GTFeatureCollection outDomain = (GTFeatureCollection) output.get(ID_OUT_DOMAIN);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of source features: " + domain.size() + "\n\t" +
                "old CRS: " + domain.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
                "new CRS: " + outDomain.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
