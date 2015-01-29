package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.provision.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.similarity.TopologyRelation;
import de.tudresden.gis.fusion.operation.similarity.geometry.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.similarity.geometry.HausdorffDistance;

public class RDFRelationsTurtleTest {

	@Test
	public void readRDFFile() throws ProcessException, MalformedURLException, URISyntaxException {
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_dd.shp").toURI())));
		Map<String,IData> output = parser.execute(input);		
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_dd.shp").toURI())));
		output = parser.execute(input);		
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		//add bbox distance
		BoundingBoxDistance process1 = new BoundingBoxDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(20));
		output = process1.execute(input);	
		IFeatureRelationCollection relations = (GTFeatureRelationCollection) output.get("OUT_RELATIONS");
				
		HausdorffDistance process = new HausdorffDistance();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process.execute(input);	
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_DATA", relations);
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
		IDataResource file = (IDataResource) output.get("OUT_FILE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print(
				"number of identified relations: " + relations.size() + "\n\t" +
				"target relation file: " + file.getIdentifier().asString() + "\n");
		
		
		input.put("IN_RDF_RESOURCE", file);
		
		RDFRelationsTurtleParser relationsParser = new RDFRelationsTurtleParser();
		output = relationsParser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_RELATIONS"));
		Assert.assertTrue(output.get("OUT_RELATIONS") instanceof IFeatureRelationCollection);
		
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Assert.assertTrue(relations.size() > 0);
		runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + parser.getProcessIRI().asString() + "\n\t" +
				"relations read from rdf: " + relations.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
		
		//add topology relation
		TopologyRelation process5 = new TopologyRelation();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		output = process5.execute(input);	
		relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
				
		input.put("IN_DATA", relations);
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
		file = (IDataResource) output.get("OUT_FILE");
		
		runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print(
				"number of identified relations: " + relations.size() + "\n\t" +
				"target relation file: " + file.getIdentifier().asString() + "\n");
	}
	
}