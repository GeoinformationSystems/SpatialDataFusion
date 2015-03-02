package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.relation.similarity.SinuosityDifference;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class SinuosityDifferenceTest {

	@Test
	public void calculateSimilarity() {
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_dd.shp").toURI()));
		output = parser.execute(input);		
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		SinuosityDifference process = new SinuosityDifference();
		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(0.1));
		output = process.execute(input);	
		IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProfile().getProcessName() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");		
	}
}
