package de.tud.fusion.operation.retrieval;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.feature.geotools.GTFeatureCollection;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.literal.URILiteral;

public class JSONParserTest {
	
	@Test
	public void readJSONFile() {
		readJSON(new URILiteral(new File("D:/Geodaten/Testdaten/json", "features.json").toURI()));	
	}
	
	private void readJSON(URILiteral resource) {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		JSONParser parser = new JSONParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof GTFeatureCollection);
		
		GTFeatureCollection collection = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		Assert.assertTrue(collection.resolve().size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getDescription().getIdentifier() + "\n\t" +
				"features read from JSON: " + collection.resolve().size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}

}
