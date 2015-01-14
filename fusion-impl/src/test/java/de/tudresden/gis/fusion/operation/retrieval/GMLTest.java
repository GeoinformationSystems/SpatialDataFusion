package de.tudresden.gis.fusion.operation.retrieval;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;

public class GMLTest {

	@Test
	public void readGMLFile() throws ProcessException, MalformedURLException, URISyntaxException {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs").toURI())));
		
		GMLParser parser = new GMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof IFeatureCollection);
		
		IFeatureCollection features = (IFeatureCollection) output.get("OUT_FEATURES");
		
		Assert.assertTrue(features.size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + parser.getProcessIRI().asString() + "\n\t" +
				"features read from gml: " + features.size() + "\n\t" +
				"gml feature bounds: " + boundsToString(features.getSpatialProperty().getBounds()) + "\n\t" +
				"gml feature crs: : " + features.getSpatialProperty().getSRSName().asString() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");		
	}
	
	private String boundsToString(double[] bounds) {
		return "[" + bounds[0] + "," + bounds[1] + " ; " + bounds[2] + "," + bounds[3] + "]";
	}
	
}
