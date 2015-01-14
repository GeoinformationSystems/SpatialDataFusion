package de.tudresden.gis.fusion.operation.retrieval;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.misc.OSMCollection;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.retrieval.OSMParser;

public class OSMTest {

	@Test
	public void readOSMOverpass() throws ProcessException, MalformedURLException, URISyntaxException {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_OSM_URL", new Resource(new IRI(new URL("http://open.mapquestapi.com/xapi/api/0.6/way%5Bbbox=13.43353,51.01806,13.44083,51.02064%5D").toURI())));
		
		OSMParser parser = new OSMParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_OSM_COLLECTION"));
		Assert.assertTrue(output.get("OUT_OSM_COLLECTION") instanceof OSMFeatureCollection);
		
		OSMFeatureCollection osmData = (OSMFeatureCollection) output.get("OUT_OSM_COLLECTION");
		OSMCollection osmFC = osmData.getOSMCollection();
		
		Assert.assertTrue(osmFC.getNodes().size() > 0);
		Assert.assertTrue(osmFC.getWays().size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + parser.getProcessIRI().asString() + "\n\t" +
				"nodes read from osm: " + osmFC.getNodes().size() + "\n\t" +
				"ways read from osm: " + osmFC.getWays().size() + "\n\t" +
				"feature bounds: " + osmFC.getNodes().getBounds() + "\n\t" +
				"feature crs: : " + osmFC.getNodes().getSchema().getCoordinateReferenceSystem().getName().toString() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
