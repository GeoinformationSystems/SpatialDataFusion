package de.tud.fusion.operation.retrieval;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.feature.IFeatureRepresentationView;
import de.tud.fusion.data.feature.IFeatureTypeView;
import de.tud.fusion.data.feature.osm.OSMFeature;
import de.tud.fusion.data.feature.osm.OSMFeatureCollection;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.literal.URILiteral;

public class OSMXMLParserTest {
	
	@Test
	public void readOSMFile() {
		readOSM(new URILiteral(new File("D:/Geodaten/Testdaten/osm", "sample.xml").toURI()));	
	}
	
	@Test
	public void readOSM_Overpass() {
		readOSM(new URILiteral(URI.create("http://overpass-api.de/api/interpreter?data=[out:xml];%28node%2851.02,13.72,51.03,13.73%29;%3C;%29;out%20meta;")));	
	}
	
	private void readOSM(URILiteral resource) {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		OSMXMLParser parser = new OSMXMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof OSMFeatureCollection);
		
		@SuppressWarnings("unchecked")
		OSMFeatureCollection<OSMFeature> osmCollection = (OSMFeatureCollection<OSMFeature>) output.get("OUT_FEATURES");
		
		Assert.assertTrue(osmCollection.getNodes().size() > 0);
		Assert.assertTrue(osmCollection.getWays().size() > 0);
		Assert.assertTrue(osmCollection.getRelations().size() > 0);
		
		Assert.assertTrue(osmCollection.getWays().iterator().next().getType() instanceof IFeatureTypeView);
		Assert.assertTrue(osmCollection.getWays().iterator().next().getRepresentation() instanceof IFeatureRepresentationView);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getDescription().getIdentifier() + "\n\t" +
				"features read from OSM: " + osmCollection.resolve().size() + "\n\t" +
				"nodes read from OSM: " + osmCollection.getNodes().size() + "\n\t" +
				"ways read from OSM: " + osmCollection.getWays().size() + "\n\t" +
				"relations read from OSM: " + osmCollection.getRelations().size() + "\n\t" +
				"bounds: " + osmCollection.getBounds()[0] + "; " + osmCollection.getBounds()[1] + "; " + osmCollection.getBounds()[2] + "; " + osmCollection.getBounds()[3] + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
