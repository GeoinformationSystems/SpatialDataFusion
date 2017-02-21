package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GMLParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readGMLFile_V21() {
        readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs100.xml").toURI()));
    }

    @Test
    public void readGMLFile_V31() {
        readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs110.xml").toURI()));
    }

    @Ignore
    public void readGMLFile_V32() {
        //TODO investigate failure
        readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs20.xml").toURI()));
    }

    @Test
    public void readWFS11() {
        readGML(new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));
    }

    private void readGML(URILiteral resource) {

        GMLParser parser = new GMLParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(OUT_FEATURES).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, resource);

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_FEATURES));
        Assert.assertTrue(output.get(ID_OUT_FEATURES) instanceof GTFeatureCollection);

        GTFeatureCollection features = (GTFeatureCollection) output.get(ID_OUT_FEATURES);
        Assert.assertTrue(features.size() > 0);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "features read from gml: " + features.size() + "\n\t" +
                "bounds: " + features.getBounds() + "\n\t" +
                "feature crs: " + (features.getReferenceSystem() != null ? features.getReferenceSystem().getName() : "not set") + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
