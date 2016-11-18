package de.tudresden.gis.fusion.operation.aggregate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.io.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.io.ShapefileParser;
import de.tudresden.gis.fusion.operation.measurement.AngleDifference;
import de.tudresden.gis.fusion.operation.measurement.BoundingBoxDistance;

public class ManualChain {

	@Test
	public void chain() throws MalformedURLException, URISyntaxException {
		
		ShapefileParser parser = new ShapefileParser();
		Map<String,IData> input = new HashMap<String,IData>();

		input.put("IN_RESOURCE", new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Geodaten/Testdaten/shape", "osm_dd.shp").toURI()));
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
		
		systemOut(process1, relations.resolve().size() + " relations");
		
		//Process 2
		input.clear();
		AngleDifference process2 = new AngleDifference();		
		input.put("IN_SOURCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_RELATIONS", relations);
		input.put("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
		input.put("IN_DROP_RELATIONS", new BooleanLiteral(true));
		output = process2.execute(input);		
		relations = (FeatureRelationCollection) output.get("OUT_RELATIONS");
		
		systemOut(process2, relations.resolve().size() + " relations");
		
		//Output
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_RDF", relations);
		input.put("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/relation#;relation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#;spatialOp;"
				+ "http://purl.org/dc/terms/#;dc;"
				+ "http://purl.org/dc/elements/1.1/#;dc11;"
				+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
		output = generator.execute(input);
		URILiteral fileURI = (URILiteral) output.get("OUT_RESOURCE");
		
		systemOut(generator, fileURI.getValue());
	
	}
	
	private Runtime runtime = Runtime.getRuntime();
	private void systemOut(AOperationInstance op, String result){
		runtime.gc();
		System.out.print(op.profile().getIdentifier() + "\n\t\t" +
				"result: " + result + "\n\t\t" +
				"process runtime (ms): " + ((LongLiteral) op.output("OUT_RUNTIME")).resolve() + "\n\t\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t");
	}
	
}
