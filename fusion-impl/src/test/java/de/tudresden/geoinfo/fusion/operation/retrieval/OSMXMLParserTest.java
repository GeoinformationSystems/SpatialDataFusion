package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.DataCollection;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.osm.OSMFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.osm.OSMRelation;
import de.tudresden.geoinfo.fusion.data.feature.osm.OSMVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class OSMXMLParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RELATIONS = "OUT_RELATIONS";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readOSMFile() throws MalformedURLException {
        readOSM(new URLLiteral(new File("D:/Geodaten/Testdaten/osm", "sample.xml").toURI().toURL()));
    }

    @Test
    public void readOSM_Overpass() throws MalformedURLException {
        readOSM(new URLLiteral("http://overpass-api.de/api/interpreter?data=[out:xml];%28node%2851.02,13.72,51.03,13.73%29;%3C;%29;out%20meta;"));
    }

    private void readOSM(URLLiteral resource) {

        OSMXMLParser parser = new OSMXMLParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_RELATIONS = parser.getOutputConnector(OUT_RELATIONS).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(OUT_FEATURES).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, resource);

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_FEATURES));
        Assert.assertTrue(output.get(ID_OUT_FEATURES) instanceof OSMFeatureCollection);
        Assert.assertTrue(output.containsKey(ID_OUT_RELATIONS));
        Assert.assertTrue(output.get(ID_OUT_RELATIONS) instanceof DataCollection);

        @SuppressWarnings("unchecked") OSMFeatureCollection<OSMVectorFeature> osmCollection = (OSMFeatureCollection) output.get(ID_OUT_FEATURES);
        @SuppressWarnings("unchecked") DataCollection<OSMRelation> osmRelations = (DataCollection<OSMRelation>) output.get(ID_OUT_RELATIONS);

        Assert.assertTrue(osmCollection.getNodes().size() > 0);
        Assert.assertTrue(osmCollection.getWays().size() > 0);
        Assert.assertTrue(osmRelations.resolve().size() > 0);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "features read from OSM: " + osmCollection.resolve().size() + "\n\t" +
                "nodes read from OSM: " + osmCollection.getNodes().size() + "\n\t" +
                "ways read from OSM: " + osmCollection.getWays().size() + "\n\t" +
                "relations read from OSM: " + osmRelations.resolve().size() + "\n\t" +
                "bounds: " + osmCollection.getBounds() + "\n\t" +
                "reference system: " + osmCollection.getReferenceSystem().getName() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
