package de.tudresden.geoinfo.fusion.operation.harmonization;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.measurement.AbstractMeasurementTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CRSReprojectTest extends AbstractMeasurementTest {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_SOURCE");
    private final static IIdentifier IN_RANGE = new Identifier("IN_TARGET");
    private final static IIdentifier IN_CRS = new Identifier("IN_CRS");

    private final static IIdentifier OUT_DOMAIN = new Identifier("OUT_SOURCE");
    private final static IIdentifier OUT_RANGE = new Identifier("OUT_TARGET");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

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

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_DOMAIN, domain);
        if(range != null)
            input.put(IN_RANGE, range);
        if(crsURI != null)
            input.put(IN_CRS, crsURI);

		CRSReproject process = new CRSReproject();

        Map<IIdentifier,IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(OUT_DOMAIN));
        Assert.assertTrue(output.get(OUT_DOMAIN) instanceof GTFeatureCollection);

        if(range != null)
            Assert.assertTrue(output.get(OUT_RANGE) instanceof GTFeatureCollection);

		GTFeatureCollection outDomain = (GTFeatureCollection) output.get(OUT_DOMAIN);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
				"number of source features: " + domain.size() + "\n\t" +
				"old CRS: " + domain.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
				"new CRS: " + outDomain.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
