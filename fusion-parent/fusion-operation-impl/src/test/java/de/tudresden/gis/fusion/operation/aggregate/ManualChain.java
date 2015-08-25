package de.tudresden.gis.fusion.operation.aggregate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.operation.geotools.AngleDifference;
import de.tudresden.gis.fusion.operation.geotools.BoundingBoxDistance;
import de.tudresden.gis.fusion.operation.geotools.io.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.geotools.io.ShapefileParser;

public class ManualChain {

	@Test
	public void chain() throws MalformedURLException, URISyntaxException {
		
		ShapefileParser parser = new ShapefileParser();
		Map<String,IData> input = new HashMap<String,IData>();

		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/shape", "osm_dd.shp").toURI()));
		output = parser.execute(input);		
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		System.out.print("TEST: Manual Chain\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t");
		
		//Process 1
		BoundingBoxDistance process1 = new BoundingBoxDistance();		
		input.put("IN_SOURCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_THRESHOLD", new DecimalLiteral(50));
		output = process1.execute(input);
		FeatureRelationCollection relations = (FeatureRelationCollection) output.get("OUT_RELATIONS");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print(process1.profile().processDescription().title() + "\n\t\t" +
				"number of identified relations: " + relations.size() + "\n\t\t" +
				"process runtime (ms): " + ((LongLiteral) process1.output("OUT_RUNTIME")).value() + "\n\t\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t");
		
		//Process 2
		input.clear();
		AngleDifference process2 = new AngleDifference();		
		input.put("IN_SOURCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
		output = process2.execute(input);		
		relations = (FeatureRelationCollection) output.get("OUT_RELATIONS");
		
		runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print(process2.profile().processDescription().title() + "\n\t\t" +
				"number of identified relations: " + relations.size() + "\n\t\t" +
				"process runtime (ms): " + ((LongLiteral) process2.output("OUT_RUNTIME")).value() + "\n\t\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t");
		
		//Output
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_RDF", relations);
		input.put("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion/relation#;relation;"
				+ "http://purl.org/dc/terms/#;dc;"
				+ "http://purl.org/dc/elements/1.1/#;dc11;"
				+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/relation/property/;property;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
		output = generator.execute(input);
		URILiteral fileURI = (URILiteral) output.get("OUT_RESOURCE");
		
		runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print(generator.profile().processDescription().title() + "\n\t\t" +
				"file URI: " + fileURI.literalValue().value() + "\n\t\t" +
				"process runtime (ms): " + ((LongLiteral) generator.output("OUT_RUNTIME")).value() + "\n\t\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t");	
	}
	
}
