//package de.tud.fusion.operation.enhancement;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import AbstractFeatureCollection;
//import org.junit.Assert;
//import org.junit.Test;
//
//import IData;
//import OSMVectorFeature;
//import OSMFeatureCollection;
//import OSMWay;
//import LongLiteral;
//import URLLiteral;
//import OSMXMLParser;
//
//public class LineIntersectionTest {
//
//	@Test
//	public void intersect() {
//
//		Map<String,IData> input = new HashMap<String,IData>();
//		input.put("IN_RESOURCE", new URLLiteral(new File("D:/Geodaten/Testdaten/osm", "sample.xml").toURI()));
//
//		OSMXMLParser parser = new OSMXMLParser();
//		Map<String,IData> output = parser.execute(input);
//
//		Assert.assertNotNull(output);
//		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
//		Assert.assertTrue(output.get("OUT_FEATURES") instanceof OSMFeatureCollection);
//
//		@SuppressWarnings("unchecked")
//		OSMFeatureCollection<OSMVectorFeature> osmCollection = (OSMFeatureCollection<OSMVectorFeature>) output.get("OUT_FEATURES");
//
//		Assert.assertTrue(osmCollection.getWays().size() > 0);
//		OSMFeatureCollection<OSMWay> ways = osmCollection.getWays();
//
//		LineIntersection process = new LineIntersection();
//
//		input.clear();
//		input.put("IN_FEATURES", ways);
//		output = process.execute(input);
//
//		Assert.assertNotNull(output);
//		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
//		Assert.assertTrue(output.get("OUT_FEATURES") instanceof AbstractFeatureCollection<?>);
//
//		AbstractFeatureCollection<?> waysIntersect = (AbstractFeatureCollection<?>) output.get("OUT_FEATURES");
//
//		Runtime runtime = Runtime.getRuntime();
//		runtime.gc();
//		System.out.print("TEST: " + process.getMetadata().getIdentifier() + "\n\t" +
//				"ways read from osm: " + ways.size() + "\n\t" +
//				"number of ways after self-intersection: " + waysIntersect.size() + "\n\t" +
//				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
//	}
//
//}
