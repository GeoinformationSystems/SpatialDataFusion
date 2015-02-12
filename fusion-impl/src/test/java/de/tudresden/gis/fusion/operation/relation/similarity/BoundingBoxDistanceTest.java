package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.operation.relation.similarity.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class BoundingBoxDistanceTest {

	@Test
	public void calculateSimilarity() {
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_highDensity.shp").toURI())));
		input.put("IN_WITH_INDEX", new BooleanLiteral(true));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_highDensity.shp").toURI())));
		output = parser.execute(input);	
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		BoundingBoxDistance process = new BoundingBoxDistance();
		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process.execute(input);	
		GTFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
}
