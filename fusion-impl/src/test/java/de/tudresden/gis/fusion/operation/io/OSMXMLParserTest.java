package de.tudresden.gis.fusion.operation.io;

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

public class OSMXMLParserTest {
	
	@Test
	public void readOSMFile() throws ProcessException {
		readOSM(new URILiteral(new File("D:/Programmierung/Testdaten/osm", "osm.xml").toURI()));	
	}
	
//	@Test
//	public void readOSM_Overpass() throws ProcessException {
//		readOSM(new URILiteral("http://overpass-api.de/api/interpreter?data=[out:xml];%28node%2851.02,13.72,51.03,13.73%29;%3C;%29;out%20meta;"));	
//	}
	
	private void readOSM(URILiteral resource) throws ProcessException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		OSMXMLParser parser = new OSMXMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_NODES"));
		Assert.assertTrue(output.get("OUT_NODES") instanceof GTFeatureCollection);
		Assert.assertTrue(output.containsKey("OUT_WAYS"));
		Assert.assertTrue(output.get("OUT_WAYS") instanceof GTFeatureCollection);
		
		GTFeatureCollection nodes = (GTFeatureCollection) output.get("OUT_NODES");
		GTFeatureCollection ways = (GTFeatureCollection) output.get("OUT_WAYS");
		
		Assert.assertTrue(nodes.size() > 0);
		Assert.assertTrue(ways.size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.profile().processDescription().getTitle() + "\n\t" +
				"nodes read from osm: " + nodes.size() + "\n\t" +
				"ways read from osm: " + ways.size() + "\n\t" +
				"ways - bounds: " + ways.collection().getBounds() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
