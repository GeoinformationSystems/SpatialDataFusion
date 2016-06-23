package de.tudresden.gis.fusion.operation.measurement;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTGridCoverage;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.operation.io.GMLParser;
import de.tudresden.gis.fusion.operation.io.GridCoverageParser;
import de.tudresden.gis.fusion.operation.io.RDFTurtleGenerator;

public class ZonalStatsTest {
	
	@Test
	public void calculateSimilarity() throws MalformedURLException, URISyntaxException {
		
		GMLParser parserGML = new GMLParser();
		GridCoverageParser parserCoverage = new GridCoverageParser();
		Map<String,IData> input = new HashMap<String,IData>();

		input.put("IN_RESOURCE", new URILiteral("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=fusion:radwege_dd&srsname=crs:84"));
		Map<String,IData> output = parserGML.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_RESOURCE", new URILiteral("http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__pm10-200607050900-wgs84-2"));
		output = parserCoverage.execute(input);		
		GTGridCoverage target = (GTGridCoverage) output.get("OUT_COVERAGE");
		
		ZonalStatistics process = new ZonalStatistics();
		
		input.put("IN_SOURCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_BAND", new IntegerLiteral(0));
		input.put("IN_BUFFER", new DecimalLiteral(0.001)); //~100m in WGS84
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_RELATIONS"));
		Assert.assertTrue(output.get("OUT_RELATIONS") instanceof FeatureRelationCollection);
		
		FeatureRelationCollection relations = (FeatureRelationCollection) output.get("OUT_RELATIONS");

		//Output
		RDFTurtleGenerator generator = new RDFTurtleGenerator();
		input.put("IN_RDF", relations);
		input.put("IN_URI_PREFIXES", new StringLiteral(""
				+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/relation#;relation;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#;spatialOp;"
				+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#;zonalStats;"
				+ "http://purl.org/dc/terms/#;dc;"
				+ "http://purl.org/dc/elements/1.1/#;dc11;"
				+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
				+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
		output = generator.execute(input);
		URILiteral fileURI = (URILiteral) output.get("OUT_RESOURCE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + process.profile().processDescription().getTitle() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" +
				"number of identified relations: " + relations.resolve().size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
				"result: " + fileURI.getValue() + "\n\t");
	}

}
