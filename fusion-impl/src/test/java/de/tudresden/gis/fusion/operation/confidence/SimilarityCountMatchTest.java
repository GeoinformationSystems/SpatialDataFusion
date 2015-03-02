package de.tudresden.gis.fusion.operation.confidence;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.relation.TopologyRelation;
import de.tudresden.gis.fusion.operation.relation.similarity.AngleDifference;
import de.tudresden.gis.fusion.operation.relation.similarity.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.relation.similarity.GeometryDistance;
import de.tudresden.gis.fusion.operation.relation.similarity.LengthDifference;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class SimilarityCountMatchTest {

	@Test
	public void calculateSimilarity() {
		
		long start = System.currentTimeMillis();
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_dd.shp").toURI()));
		output = parser.execute(input);		
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		//add bbox distance
		BoundingBoxDistance process1 = new BoundingBoxDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process1.execute(input);	
		GTFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add angle difference
		AngleDifference process2 = new AngleDifference();		
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
		input.put("IN_DROP_RELATIONS", new BooleanLiteral(true));
		output = process2.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add geometry distance
		GeometryDistance process3 = new GeometryDistance();		
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		input.put("IN_DROP_RELATIONS", new BooleanLiteral(false));
		output = process3.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add length difference
		LengthDifference process4 = new LengthDifference();		
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(20));
		output = process4.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add topology relation
		TopologyRelation process5 = new TopologyRelation();		
		input.put("IN_RELATIONS", relations);
		output = process5.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add confidence count
		SimilarityCountMatch process6 = new SimilarityCountMatch();		
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new IntegerLiteral(4));
		output = process6.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//write output
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process6.getProfile().getProcessName() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process6.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"total runtime (ms): " + (System.currentTimeMillis() - start) + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
	}
	
}
