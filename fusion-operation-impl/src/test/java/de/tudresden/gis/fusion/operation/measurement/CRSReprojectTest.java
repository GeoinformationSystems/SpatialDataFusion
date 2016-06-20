package de.tudresden.gis.fusion.operation.measurement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.harmonization.CRSReproject;
import de.tudresden.gis.fusion.operation.io.ShapefileParser;

public class CRSReprojectTest {
	
	@Test
	public void reproject() throws MalformedURLException, URISyntaxException {
		
		ShapefileParser parser = new ShapefileParser();
		Map<String,IData> input = new HashMap<String,IData>();

		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		CRSReproject process = new CRSReproject();
		
		input.put("IN_SOURCE", reference);
		input.put("IN_CRS", new URILiteral(URI.create("http://www.opengis.net/def/crs/EPSG/0/4326")));
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_SOURCE"));
		Assert.assertTrue(output.get("OUT_SOURCE") instanceof GTFeatureCollection);
		
		GTFeatureCollection outSource = (GTFeatureCollection) output.get("OUT_SOURCE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + process.profile().processDescription().getTitle() + "\n\t" +
				"number of source features: " + reference.size() + "\n\t" +
				"old CRS: " + reference.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
				"new CRS: " + outSource.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}

}
