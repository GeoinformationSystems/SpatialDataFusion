package de.tud.fusion.operation.retrieval;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import de.tud.fusion.data.IData;
import de.tud.fusion.data.feature.geotools.GTFeatureCollection;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.literal.URILiteral;

public class GMLParserTest {

	@Test
	public void readGMLFile_V21() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs100.xml").toURI()));	
	}
	
	@Test
	public void readGMLFile_V31() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs110.xml").toURI()));	
	}
	
	@Test
	public void readGMLFile_V32() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs20.xml").toURI()));
	}
	
	@Test
	public void readWFS11() {
		readGML(new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));	
	}
	
	private void readGML(URILiteral resource) {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		GMLParser parser = new GMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof GTFeatureCollection);
		
		GTFeatureCollection features = (GTFeatureCollection) output.get("OUT_FEATURES");
		Assert.assertTrue(features.size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getDescription().getIdentifier() + "\n\t" +
				"features read from gml: " + features.size() + "\n\t" +
				"bounds: " + features.collection().getBounds() + "\n\t" +
				"feature crs: " + (features.collection().getSchema().getCoordinateReferenceSystem() != null ? features.collection().getSchema().getCoordinateReferenceSystem().getName() : "not set") + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
