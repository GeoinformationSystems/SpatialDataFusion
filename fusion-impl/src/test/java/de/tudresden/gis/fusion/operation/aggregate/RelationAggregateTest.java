package de.tudresden.gis.fusion.operation.aggregate;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.provision.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;

public class RelationAggregateTest {

	@Test
	public void aggregateRelations() {
		
		Map<String,IData> input = new HashMap<String,IData>();
		GMLParser parser = new GMLParser();
		input.put("IN_RESOURCE", new Resource(new IRI("http://localhost:8081/geoserver/fusion/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=fusion:atkis_dd")));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");	
		input.put("IN_RESOURCE", new Resource(new IRI("http://localhost:8081/geoserver/fusion/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=fusion:osm_dd")));
		output = parser.execute(input);
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");	
		
		
		RelationAggregate process = new RelationAggregate();
		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_OPERATIONS", new StringLiteral(
				"BoundingBoxDistance,IN_THRESHOLD,50,IN_DROP_RELATIONS,true;" +
				"AngleDifference,IN_THRESHOLD,0.063,IN_DROP_RELATIONS,false;" +
				"TopologyRelation"
				));
		output = process.execute(input);	
		IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_RELATIONS", relations);
		input.put("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
		output = generator.execute(input);	
		IDataResource file = (IDataResource) output.get("OUT_RESOURCE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
				"target relation file: " + file.getIdentifier().asString() + "\n");
		
	}
	
}
