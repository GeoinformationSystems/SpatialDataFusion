package de.tudresden.geoinfo.fusion.operation.harmonization;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.mapping.TopologyRelation;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser.readShapefile;

public class CRSReprojectTest extends AbstractTest {

    private final static String IN_DOMAIN = "IN_DOMAIN";
    private final static String IN_RANGE = "IN_RANGE";
    private final static String IN_CRS = "IN_CRS";

    private final static String OUT_DOMAIN = "OUT_DOMAIN";
    private final static String OUT_RANGE = "OUT_RANGE";

    @Test
    public void reprojectWithRangeCRS() throws IOException {
        reproject(
                readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                readShapefile(new File("src/test/resources/lines1_gk.shp").toURI().toURL(), true),
                null);
    }

    @Test
    public void reprojectWithCustomCRS() throws IOException {
        reproject(
                readShapefile(new File("src/test/resources/lines1_gk.shp").toURI().toURL(), true),
                null,
                new URLLiteral(new URL("http://www.opengis.net/def/crs/EPSG/0/4326")));
    }

    @Test
    public void reprojectWithCustomCRSAndRange() throws IOException {
        reproject(
                readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true),
                readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true),
                new URLLiteral(new URL("http://www.opengis.net/def/crs/EPSG/0/31469")));
    }

    private void reproject(GTFeatureCollection domain, GTFeatureCollection range, URLLiteral crsURI) {

        AbstractOperation operation = new TopologyRelation(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_DOMAIN, domain);
        if(range != null)
            inputs.put(IN_RANGE, range);
        if(crsURI != null)
            inputs.put(IN_CRS, crsURI);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_DOMAIN, GTFeatureCollection.class);
        if (range != null)
            outputs.put(OUT_RANGE, GTFeatureCollection.class);

        this.execute(operation, inputs, outputs);

    }
}
