package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class JSONParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_FEATURES = "OUT_FEATURES";

    @Test
    public void readJSONFile() throws MalformedURLException {
        readJSON(new URLLiteral(new File("src/test/resources/polygons.json").toURI().toURL()));
    }

    private void readJSON(URLLiteral resource) {

        AbstractOperation operation = new JSONParser();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
