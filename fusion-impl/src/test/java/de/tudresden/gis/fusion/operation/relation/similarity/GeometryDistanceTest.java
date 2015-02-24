package de.tudresden.gis.fusion.operation.relation.similarity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.provision.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.relation.similarity.GeometryDistance;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;

public class GeometryDistanceTest {
	
	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		//ShapefileParser parser = new ShapefileParser();
		GMLParser parser = new GMLParser();
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_RESOURCE", new Resource(new IRI("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));
		Map<String,IData> output = parser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");

		input.put("IN_RESOURCE", new Resource(new IRI("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));
		output = parser.execute(input);
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
//		input.put("IN_SHAPE_RESOURCE", new URIData(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_highDensity.shp").toURI()));
//		Map<String,IData> output = parser.execute(input);		
//		GTFeature reference = (GTFeature) output.get("OUT_FEATURES");
//		
//		input.put("IN_SHAPE_RESOURCE", new URIData(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_highDensity.shp").toURI()));
//		output = parser.execute(input);		
//		GTFeature target = (GTFeature) output.get("OUT_FEATURES");
		
		GeometryDistance process = new GeometryDistance();
		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(0.005));
		output = process.execute(input);	
		IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");		
	}
	
//	executing de.tudresden.gis.fusion.operation.similarity.GeometryDistance
//	number of reference features: 444
//	number of target features: 444
//	number of identified relations: 120034
//	process runtime (ms): 3884
//	memory usage (mb): 457

	@Ignore
	public void calculateSimilarityCOBWEB() throws MalformedURLException, URISyntaxException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		GMLParser gmlParser = new GMLParser();
		
		input.put("IN_GML_URL", new Resource(new IRI("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));
		Map<String,IData> output = gmlParser.execute(input);
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_GML_URL", new Resource(new IRI("http://lle.wales.gov.uk/services/cobweb/wfs?SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=cobweb:dyfi_biosphere&srsName=urn:x-ogc:def:crs:EPSG:4326&filter=%3CPropertyIsEqualTo%3E%3CPropertyName%3Eclass_name%3C/PropertyName%3E%3CLiteral%3EWoodland%3C/Literal%3E%3C/PropertyIsEqualTo%3E")));
		output = gmlParser.execute(input);
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		GeometryDistance process = new GeometryDistance();
		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(0.005));
		output = process.execute(input);	
		IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_DATA", relations);
		input.put("URI_BASE", new Resource(new IRI("http://tu-dresden.de/uw/geo/gis/fusion#")));
		input.put("URI_PREFIXES", new StringLiteral(""
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/TR/xmlschema11-2#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/confidence#;confidence;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
		output = generator.execute(input);	
		Resource file = (Resource) output.get("OUT_FILE");
		
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"target relation file: " + file.getIdentifier() + "\n");
		
	}
	
}
