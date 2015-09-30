package de.tudresden.gis.fusion.operation.geotools.io;

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

public class ShapefileParserTest {

	@Test
	public void readShapeFile() throws ProcessException {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "atkis_dd.shp").toURI()));
		
		ShapefileParser parser = new ShapefileParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof GTFeatureCollection);
		
		GTFeatureCollection features = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		Assert.assertTrue(features.size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.profile().processDescription().getTitle() + "\n\t" +
				"features read from shape: " + features.size() + "\n\t" +
				"bounds: " + features.collection().getBounds() + "\n\t" +
				"shape feature crs: : " + features.collection().getSchema().getCoordinateReferenceSystem().getName() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
