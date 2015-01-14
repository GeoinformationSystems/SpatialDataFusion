package de.tudresden.gis.fusion.operation.provision;

import java.io.File;
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
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;
import de.tudresden.gis.fusion.operation.similarity.TopologyRelation;
import de.tudresden.gis.fusion.operation.similarity.geometry.AngleDifference;
import de.tudresden.gis.fusion.operation.similarity.geometry.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.GeometryDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.HausdorffDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.LengthDifference;

public class FusekiTripleStoreGeneratorTest {

	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		String FUSEKI_URI = "http://localhost:3030/fusion/update";
		
//		ShapefileParser parser = new ShapefileParser();
		GMLParser parser = new GMLParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
//		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_highDensity.shp").toURI())));
//		Map<String,IData> output = parser.execute(input);		
//		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=atkis_dd").toURI())));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
//		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_highDensity.shp").toURI())));
//		output = parser.execute(input);		
//		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=osm_dd").toURI())));
		output = parser.execute(input);
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		//add bbox distance
		BoundingBoxDistance process1 = new BoundingBoxDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process1.execute(input);	
		IFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
				
		
		HausdorffDistance process = new HausdorffDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process.execute(input);	
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");		
		
		//add angle difference
		AngleDifference process2 = new AngleDifference();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
		output = process2.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add geometry distance
		GeometryDistance process3 = new GeometryDistance();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process3.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add length difference
		LengthDifference process4 = new LengthDifference();	
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(20));
		output = process4.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add topology relation
		TopologyRelation process5 = new TopologyRelation();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		output = process5.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add confidence count
		SimilarityCountMatch process6 = new SimilarityCountMatch();
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new IntegerLiteral(4));
		output = process6.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		FusekiTripleStoreGenerator generator = new FusekiTripleStoreGenerator();
		input.put("IN_DATA", relations);
		input.put("IN_TRIPLE_STORE", new Resource(new IRI(URI.create(FUSEKI_URI))));
		input.put("IN_CLEAR_STORE", new BooleanLiteral(true));
		input.put("URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/TR/xmlschema11-2#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/confidence/statisticalConfidence#;statisticalConfidence;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
		output = generator.execute(input);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + generator.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
}
