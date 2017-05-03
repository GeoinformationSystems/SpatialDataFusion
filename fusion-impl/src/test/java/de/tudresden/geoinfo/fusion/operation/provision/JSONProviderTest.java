package de.tudresden.geoinfo.fusion.operation.provision;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class JSONProviderTest extends AbstractTest {

    private final static String IN_FEATURES = "IN_FEATURES";

    private final static String OUT_RESOURCE = "OUT_RESOURCE";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void writeJSON() throws MalformedURLException {
        writeJSON(readShapefile(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI().toURL(), true));
    }

    public void writeJSON(GTFeatureCollection features) {

        JSONProvider process = new JSONProvider();
        IIdentifier ID_IN_FEATURES = process.getInputConnector(IN_FEATURES).getIdentifier();
        IIdentifier ID_OUT_RESOURCE = process.getOutputConnector(OUT_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_FEATURES, features);

        Map<IIdentifier, IData> output = process.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_RESOURCE));
        Assert.assertTrue(output.get(ID_OUT_RESOURCE) instanceof URLLiteral);

        URLLiteral uriLiteral = (URLLiteral) output.get(ID_OUT_RESOURCE);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
                "number of features: " + features.size() + "\n\t" +
                "output file: " + uriLiteral.resolve().toString() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
