package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ElementState;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShapefileParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readShapefile() {
        readShapefile(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()), false);
    }

    @Test
    public void readShapefileWithIndex() {
        readShapefile(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()), true);
    }

    private void readShapefile(URILiteral resource, boolean index) {

        ShapefileParser parser = new ShapefileParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_IN_WITH_INDEX = parser.getInputConnector(IN_WITH_INDEX).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(OUT_FEATURES).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, resource);
        input.put(ID_IN_WITH_INDEX, new BooleanLiteral(index));

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertTrue(parser.getState().equals(ElementState.SUCCESS));
        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_FEATURES));
        if (index)
            Assert.assertTrue(output.get(ID_OUT_FEATURES) instanceof GTIndexedFeatureCollection);
        else
            Assert.assertTrue(output.get(ID_OUT_FEATURES) instanceof GTFeatureCollection);

        GTFeatureCollection collection = (GTFeatureCollection) output.get(ID_OUT_FEATURES);

        Assert.assertTrue(collection.resolve().size() > 0);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "features read from Shapefile: " + collection.resolve().size() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
