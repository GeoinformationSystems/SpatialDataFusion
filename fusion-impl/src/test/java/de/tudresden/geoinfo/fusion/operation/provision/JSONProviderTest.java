package de.tudresden.geoinfo.fusion.operation.provision;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONProviderTest extends AbstractTest {

    private final static String IN_FEATURES = "IN_FEATURES";

    private final static String OUT_RESOURCE = "OUT_RESOURCE";

    @Test
    public void writeJSON() throws IOException {
        GTFeatureCollection resource = ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true);
        writeJSON(resource);
    }

    private void writeJSON(GTFeatureCollection resource) {

        AbstractOperation operation = new JSONProvider(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_FEATURES, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_RESOURCE, URLLiteral.class);

        this.execute(operation, inputs, outputs);

    }

}
