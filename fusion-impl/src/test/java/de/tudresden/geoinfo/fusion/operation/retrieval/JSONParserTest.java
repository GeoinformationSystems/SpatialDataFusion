package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JSONParserTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readJSONFile() {
		readJSON(new URILiteral(new File("D:/Geodaten/Testdaten/json", "features.json").toURI()));
	}

	private void readJSON(URILiteral resource) {

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_RESOURCE, resource);

		JSONParser parser = new JSONParser();
        Map<IIdentifier,IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(OUT_FEATURES));
        Assert.assertTrue(output.get(OUT_FEATURES) instanceof GTFeatureCollection);

        GTFeatureCollection collection = (GTFeatureCollection) output.get(OUT_FEATURES);

        Assert.assertTrue(collection.resolve().size() > 0);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"features read from JSON: " + collection.resolve().size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
