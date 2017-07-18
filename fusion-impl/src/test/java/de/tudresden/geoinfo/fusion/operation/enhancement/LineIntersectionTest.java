package de.tudresden.geoinfo.fusion.operation.enhancement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LineIntersectionTest extends AbstractTest {

    private final static String IN_FEATURES = "IN_FEATURES";
    private final static String OUT_FEATURES = "OUT_FEATURES";

	@Test
	public void intersect() throws IOException {
        runIntersection(
                ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true));
    }

    private void runIntersection(GTFeatureCollection features) {

        AbstractOperation operation = new LineIntersection();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_FEATURES, features);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
