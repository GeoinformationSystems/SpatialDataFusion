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
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.provision.FusekiTripleStoreGenerator;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;
import de.tudresden.gis.fusion.operation.similarity.TopologyRelation;
import de.tudresden.gis.fusion.operation.similarity.geometry.LengthInPolygon;

public class CyclewayWorkflow {

	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		String FUSEKI_URI = "http://localhost:3030/fusion/update";
		long totalMS = 0;
		
		GMLParser parser = new GMLParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=radwege_dd").toURI())));
		input.put("IN_WITH_INDEX", new BooleanLiteral(true));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection radwege = (IFeatureCollection) output.get("OUT_FEATURES");		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		totalMS += ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing GMLParser \n\t" +
				"runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=laermkartierung").toURI())));
		input.put("IN_WITH_INDEX", new BooleanLiteral(true));
		output = parser.execute(input);
		IFeatureCollection laermkartierung = (IFeatureCollection) output.get("OUT_FEATURES");
		runtime.gc();
		totalMS += ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing GMLParser \n\t" +
				"runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
//		input.put("IN_GML_URL", new Resource(new IRI(new URL("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=airQuality").toURI())));
//		input.put("IN_WITH_INDEX", new BooleanLiteral(true));
//		output = parser.execute(input);
//		IFeatureCollection airQuality = (IFeatureCollection) output.get("OUT_FEATURES");
//		runtime.gc();
//		totalMS += ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue();
//		System.out.print("executing GMLParser \n\t" +
//				"runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//get topology relation
		TopologyRelation process1 = new TopologyRelation();		
		input.put("IN_REFERENCE", radwege);
		input.put("IN_TARGET", laermkartierung);
		output = process1.execute(input);	
		IFeatureRelationCollection radwege_laermkartierung = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");		
		runtime.gc();
		totalMS += ((LongLiteral) process1.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing TopologyRelation \n\t" +
				"number of identified relations: " + radwege_laermkartierung.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process1.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
//		TopologyRelation process2 = new TopologyRelation();		
//		input.put("IN_REFERENCE", radwege);
//		input.put("IN_TARGET", airQuality);
//		output = process2.execute(input);	
//		IFeatureRelationCollection radwege_airQuality = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");		
//		runtime.gc();
//		totalMS += ((LongLiteral) process2.getOutput("OUT_RUNTIME")).getValue();
//		System.out.print("executing TopologyRelation \n\t" +
//				"number of identified relations: " + radwege_airQuality.size() + "\n\t" +
//				"runtime (ms): " + ((LongLiteral) process2.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//get length in polygon
		LengthInPolygon process3 = new LengthInPolygon();		
		input.put("IN_REFERENCE", radwege);
		input.put("IN_TARGET", laermkartierung);
		input.put("IN_RELATIONS", radwege_laermkartierung);
		output = process3.execute(input);	
		radwege_laermkartierung = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");		
		runtime.gc();
		totalMS += ((LongLiteral) process3.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing LengthInPolygon \n\t" +
				"number of identified relations: " + radwege_laermkartierung.size() + "\n\t" +
				"runtime (ms): " + ((LongLiteral) process3.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
//		LengthInPolygon process4 = new LengthInPolygon();		
//		input.put("IN_REFERENCE", radwege);
//		input.put("IN_TARGET", airQuality);
//		input.put("IN_RELATIONS", radwege_airQuality);
//		output = process4.execute(input);	
//		radwege_airQuality = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");		
//		runtime.gc();
//		totalMS += ((LongLiteral) process4.getOutput("OUT_RUNTIME")).getValue();
//		System.out.print("executing LengthInPolygon \n\t" +
//				"number of identified relations: " + radwege_airQuality.size() + "\n\t" +
//				"runtime (ms): " + ((LongLiteral) process4.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		//write outputs
		FusekiTripleStoreGenerator generator = new FusekiTripleStoreGenerator();
		input.put("IN_DATA", radwege_laermkartierung);
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
		
//		input.put("IN_DATA", radwege_airQuality);
//		input.put("IN_TRIPLE_STORE", new Resource(new IRI(URI.create(FUSEKI_URI))));
//		input.put("IN_CLEAR_STORE", new BooleanLiteral(false));
//		input.put("URI_PREFIXES", new StringLiteral(""
//				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
//				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
//				+ "http://www.w3.org/2001/XMLSchema#;xsd;"
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/confidence/statisticalConfidence#;statisticalConfidence;"
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
//				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
//		output = generator.execute(input);
		
		runtime.gc();
		totalMS += ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue();
		System.out.print("executing " + generator.getProcessIRI().asString() + "\n\t" +
				"number of radweg features: " + radwege.size() + "\n\t" + 
				"number of laermkartierung features: " + laermkartierung.size() + "\n\t" +
//				"number of airQuality features: " + airQuality.size() + "\n\t" +
				"number of identified relations radwege_laermkartierung: " + radwege_laermkartierung.size() + "\n\t" +
//				"number of identified relations radwege_airQuality: " + radwege_airQuality.size() + "\n\t" +
				"generator runtime (ms): " + ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		System.out.print("total runtime (ms): " + totalMS);
		
	}
	
}
