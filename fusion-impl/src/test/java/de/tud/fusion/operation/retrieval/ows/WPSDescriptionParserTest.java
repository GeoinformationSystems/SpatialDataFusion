package de.tud.fusion.operation.retrieval.ows;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.data.ows.WPSProcessDescription;

public class WPSDescriptionParserTest {
	
	@Test
	public void readWPSDescription() {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?Request=describeprocess&Service=WPS&version=1.0.0&identifier=de.tudresden.gis.fusion.algorithm.BoundingBoxDistance")));
		
		WPSDescriptionParser parser = new WPSDescriptionParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_DESCRIPTION"));
		Assert.assertTrue(output.get("OUT_DESCRIPTION") instanceof WPSProcessDescription);
		
		WPSProcessDescription description = (WPSProcessDescription) output.get("OUT_DESCRIPTION");
		Assert.assertTrue(description.getProcessIdentifier().size() == 1);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getDescription().getIdentifier() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
