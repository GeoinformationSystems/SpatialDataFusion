package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.osm.OSMFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class OSMXMLParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RELATIONS = "OUT_RELATIONS";

    @Test
    public void readOSM() throws MalformedURLException {
        readOSM(new URLLiteral(new File("src/test/resources/osm.xml").toURI().toURL()));
    }

    @Test
    public void readOSM_API() throws MalformedURLException {
        readOSM(new URLLiteral("http://overpass-api.de/api/interpreter?data=[out:xml];%28node%2851.02,13.72,51.03,13.73%29;%3C;%29;out%20meta;"));
    }

    private void readOSM(URLLiteral resource) {

        AbstractOperation operation = new OSMXMLParser(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, OSMFeatureCollection.class);
        outputs.put(OUT_RELATIONS, DataCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
