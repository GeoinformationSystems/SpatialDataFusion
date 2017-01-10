package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Graph;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.osm.OSMFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class OSMXMLParserTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
	private final static IIdentifier OUT_RELATIONS = new Identifier("OUT_RELATIONS");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readOSMFile() {
		readOSM(new URILiteral(new File("D:/Geodaten/Testdaten/osm", "sample.xml").toURI()));
	}

	@Test
	public void readOSM_Overpass() {
		readOSM(new URILiteral(URI.create("http://overpass-api.de/api/interpreter?data=[out:xml];%28node%2851.02,13.72,51.03,13.73%29;%3C;%29;out%20meta;")));
	}

	private void readOSM(URILiteral resource) {

		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, resource);

		OSMXMLParser parser = new OSMXMLParser();
		Map<IIdentifier,IData> output = parser.execute(input);

		Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(OUT_FEATURES));
        Assert.assertTrue(output.get(OUT_FEATURES) instanceof OSMFeatureCollection);
        Assert.assertTrue(output.containsKey(OUT_RELATIONS));
        Assert.assertTrue(output.get(OUT_RELATIONS) instanceof Graph);

        OSMFeatureCollection osmCollection = (OSMFeatureCollection) output.get(OUT_FEATURES);
        Graph osmRelations = (Graph) output.get(OUT_RELATIONS);

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
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
