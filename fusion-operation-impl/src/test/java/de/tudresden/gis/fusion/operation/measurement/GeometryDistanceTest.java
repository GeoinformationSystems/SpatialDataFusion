package de.tudresden.gis.fusion.operation.measurement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.operation.io.ShapefileParser;
import de.tudresden.gis.fusion.operation.measurement.GeometryDistance;

public class GeometryDistanceTest {
	
	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		ShapefileParser parser = new ShapefileParser();
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_WITH_INDEX", new BooleanLiteral(true));

		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "osm_dd.shp").toURI()));
		output = parser.execute(input);		
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		GeometryDistance process = new GeometryDistance();
		
		input.put("IN_SOURCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_RELATIONS"));
		Assert.assertTrue(output.get("OUT_RELATIONS") instanceof FeatureRelationCollection);
		
		FeatureRelationCollection relations = (FeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + process.profile().processDescription().getTitle() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");		
	}

}
