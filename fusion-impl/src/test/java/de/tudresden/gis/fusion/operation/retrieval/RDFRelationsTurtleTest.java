package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.confidence.SimilarityCountMatch;
import de.tudresden.gis.fusion.operation.provision.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.relation.TopologyRelation;
import de.tudresden.gis.fusion.operation.relation.similarity.BoundingBoxDistance;

public class RDFRelationsTurtleTest {

	@Test
	public void readRDFFile() throws ProcessException, MalformedURLException, URISyntaxException {
		
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
		output = process1.execute(input);	
		IFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		//add confidence Measurement
		SimilarityCountMatch process6 = new SimilarityCountMatch();		
		input.put("IN_RELATIONS", relations);
		output = process6.execute(input);	
		relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_RDF", relations);
		input.put("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/confidence/statisticalConfidence#;statisticalConfidence;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#;spatialRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#;topologyRelation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#;stringRelation"));
		output = generator.execute(input);	
		URILiteral file = (URILiteral) output.get("OUT_RESOURCE");
		
		System.out.print(
				"executing " + generator.getProfile().getProcessName() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"target relation file: " + file.getIdentifier() + "\n");
		
		
		input.put("IN_RESOURCE", file);
		
		RDFRelationsTurtleParser relationsParser = new RDFRelationsTurtleParser();
		output = relationsParser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_RELATIONS"));
		Assert.assertTrue(output.get("OUT_RELATIONS") instanceof IFeatureRelationCollection);
		
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Assert.assertTrue(relations.size() > 0);
		System.out.print("executing " + relationsParser.getProfile().getProcessName() + "\n\t" +
				"relations read from rdf: " + relations.size() + "\n");
		
		//add measurement
		TopologyRelation process = new TopologyRelation();
		input.put("IN_RELATIONS", relations);
		output = process.execute(input);	
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
				
		input.put("IN_RDF", relations);
		output = generator.execute(input);	
		file = (URILiteral) output.get("OUT_RESOURCE");
		
		System.out.print(
				"executing " + generator.getProfile().getProcessName() + "\n\t" +
				"number of identified relations: " + relations.size() + "\n\t" +
				"target relation file: " + file.getIdentifier() + "\n");
	}
	
}
