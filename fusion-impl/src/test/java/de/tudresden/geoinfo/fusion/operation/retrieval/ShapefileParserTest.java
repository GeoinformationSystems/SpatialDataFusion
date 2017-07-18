package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ShapefileParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";

    private final static String OUT_FEATURES = "OUT_FEATURES";

    @Test
    public void readShapefile() throws MalformedURLException {
        readShapefile(new URLLiteral(new File("src/test/resources/lines1.shp").toURI().toURL()), false);
    }

    @Test
    public void readShapefileWithIndex() throws MalformedURLException {
        readShapefile(new URLLiteral(new File("src/test/resources/lines1.shp").toURI().toURL()), true);
    }

    public void readShapefile(URLLiteral resource, boolean withIndex) {

        AbstractOperation operation = new ShapefileParser();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);
        inputs.put(IN_WITH_INDEX, new BooleanLiteral(withIndex));

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
