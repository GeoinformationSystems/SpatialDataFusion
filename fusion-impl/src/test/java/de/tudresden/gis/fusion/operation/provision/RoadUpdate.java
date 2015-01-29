package de.tudresden.gis.fusion.operation.provision;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.confidence.SimilarityCountMatch;
import de.tudresden.gis.fusion.operation.provision.FusekiTripleStoreGenerator;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;
import de.tudresden.gis.fusion.operation.similarity.TopologyRelation;
import de.tudresden.gis.fusion.operation.similarity.geometry.AngleDifference;
import de.tudresden.gis.fusion.operation.similarity.geometry.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.GeometryDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.HausdorffDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.LengthDifference;

public class RoadUpdate {

	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		String input_size = "full";	//dd, med, full
		
		String FUSEKI_URI = "http://localhost:3030/fusion/update";
		long totalMS = 0;
		
		GMLParser parser = new GMLParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=atkis_" + input_size).toURI())));
		input.put("IN_WITH_INDEX", new BooleanLiteral(true));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		totalMS += ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing GMLParser \n\t" +
				"runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=osm_" + input_size).toURI())));
		output = parser.execute(input);
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		runtime.gc();
		totalMS += ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing GMLParser \n\t" +
				"runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add bbox distance
		BoundingBoxDistance process1 = new BoundingBoxDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process1.execute(input);	
		IFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");	
		
		runtime.gc();
		totalMS += ((LongLiteral) process1.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing BoundingBoxDistance \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process1.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add angle difference
		AngleDifference process2 = new AngleDifference();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_HARDCRITERION", new BooleanLiteral(true));
		input.put("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
		output = process2.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		runtime.gc();
		totalMS += ((LongLiteral) process2.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing AngleDifference \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process2.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add geometry distance
		GeometryDistance process3 = new GeometryDistance();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process3.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		runtime.gc();
		totalMS += ((LongLiteral) process3.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing GeometryDistance \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process3.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add length difference
		LengthDifference process4 = new LengthDifference();	
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(20));
		output = process4.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		runtime.gc();
		totalMS += ((LongLiteral) process4.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing LengthDifference \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process4.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		HausdorffDistance process = new HausdorffDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process.execute(input);	
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");		
		runtime.gc();
		totalMS += ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing HausdorffDistance \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add topology relation
		TopologyRelation process5 = new TopologyRelation();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		output = process5.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");		
		runtime.gc();
		totalMS += ((LongLiteral) process5.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing TopologyRelation \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process5.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//add confidence count
		SimilarityCountMatch process6 = new SimilarityCountMatch();
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new IntegerLiteral(4));
		output = process6.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		runtime.gc();
		totalMS += ((LongLiteral) process6.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing SimilarityCountMatch \n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process6.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		FusekiTripleStoreGenerator generator = new FusekiTripleStoreGenerator();
		input.put("IN_DATA", relations);
		input.put("IN_TRIPLE_STORE", new Resource(new IRI(URI.create(FUSEKI_URI))));
		input.put("IN_CLEAR_STORE", new BooleanLiteral(true));
		input.put("URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/confidence/statisticalConfidence#;statisticalConfidence;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
		output = generator.execute(input);
		
		runtime.gc();
		totalMS += ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing " + generator.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"generator runtime (ms): " + ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		System.out.print("total runtime (ms): " + totalMS);
	}
	
}