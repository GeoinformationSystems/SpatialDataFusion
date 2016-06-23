package de.tudresden.gis.fusion.operation.enhancement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.io.OSMXMLParser;

public class LineIntersectionTest {
	
	@Test
	public void readOSM() throws ProcessException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Programmierung/Testdaten/osm", "osm.xml").toURI()));
		
		OSMXMLParser parser = new OSMXMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_WAYS"));
		Assert.assertTrue(output.get("OUT_WAYS") instanceof GTFeatureCollection);
		
		GTFeatureCollection ways = (GTFeatureCollection) output.get("OUT_WAYS");

		Assert.assertTrue(ways.size() > 0);
		
		LineIntersection process = new LineIntersection();
		
		input.put("IN_FEATURES", ways);
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof GTFeatureCollection);
		
		GTFeatureCollection waysIntersect = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + process.profile().processDescription().getTitle() + "\n\t" +
				"ways read from osm: " + ways.size() + "\n\t" +
				"number of intersected ways: " + waysIntersect.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}

}
