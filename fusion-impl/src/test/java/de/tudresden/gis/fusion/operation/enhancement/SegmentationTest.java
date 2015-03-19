package de.tudresden.gis.fusion.operation.enhancement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class SegmentationTest {

	@Test
	public void segmentfeatures() {
		
		ShapefileParser parser = new ShapefileParser();		
		Map<String,IData> input = new HashMap<String,IData>();		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		IFeatureCollection features = (IFeatureCollection) output.get("OUT_FEATURES");
		
		Segmentation process = new Segmentation();
		input.put("IN_FEATURES", features);
		output = process.execute(input);
		IFeatureCollection segments = (IFeatureCollection) output.get("OUT_FEATURES");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProfile().getProcessName() + "\n\t" +
				"number of input features: " + features.size() + "\n\t" +
				"number of segments: " + segments.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
	}
	
}
