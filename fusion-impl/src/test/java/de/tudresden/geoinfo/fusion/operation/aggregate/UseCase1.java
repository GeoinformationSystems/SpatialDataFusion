//package de.tudresden.gis.fusion.operation.aggregate;
//
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Test;
//
//import de.tudresden.gis.fusion.data.IData;
//import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
//import de.tudresden.gis.fusion.data.feature.geotools.GTGridCoverage;
//import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
//import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
//import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
//import de.tudresden.gis.fusion.data.literal.StringLiteral;
//import de.tudresden.gis.fusion.data.literal.URLLiteral;
//import de.tudresden.gis.fusion.data.relation.BinaryRelationCollection;
//import de.tudresden.gis.fusion.operation.io.GMLParser;
//import de.tudresden.gis.fusion.operation.io.RDFTurtleGenerator;
//import de.tudresden.gis.fusion.operation.measurement.IntersectionLength;
//import de.tudresden.gis.fusion.operation.measurement.ZonalStatistics;
//
//public class UseCase1 {
//
//	private boolean tripleStore = false;
//
//	@Test
//	public void chain() throws MalformedURLException, URISyntaxException {
//
//		GMLParser parserGML = new GMLParser();
//		GridCoverageParser parserCoverage = new GridCoverageParser();
//		BinaryRelationCollection relations;
//
//		Map<String,IData> input = new HashMap<String,IData>();
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=fusion:radwege_dd&srsname=crs:84"));
//		Map<String,IData> output = parserGML.execute(input);
//		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
//
//		//noise
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=fusion:laermkartierung&srsname=crs:84"));
//		output = parserGML.execute(input);
//		GTFeatureCollection targetNoise = (GTFeatureCollection) output.get("OUT_FEATURES");
//
//		IntersectionLength lengthInPolygon = new IntersectionLength();
//		input.setRDFProperty("IN_SOURCE", reference);
//		input.setRDFProperty("IN_TARGET", targetNoise);
//		output = lengthInPolygon.execute(input);
//
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//
//		//pm10 - 09:00
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral("http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__pm10-200607050900-wgs84-2"));
//		output = parserCoverage.execute(input);
//		GTGridCoverage targetPM10_09 = (GTGridCoverage) output.get("OUT_COVERAGE");
//
//		ZonalStatistics zonalStats = new ZonalStatistics();
//
//		input.setRDFProperty("IN_SOURCE", reference);
//		input.setRDFProperty("IN_TARGET", targetPM10_09);
//		input.setRDFProperty("IN_BAND", new IntegerLiteral(0));
//		input.setRDFProperty("IN_BUFFER", new DecimalLiteral(0.0005)); //~50m in WGS84
//		output = zonalStats.execute(input);
//
//		relations.addAll(((BinaryRelationCollection) output.get("OUT_RELATIONS")).resolve());
//
//		//pm10 - 12:00
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral("http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__pm10-200607051200-wgs84-2"));
//		output = parserCoverage.execute(input);
//		GTGridCoverage targetPM10_12 = (GTGridCoverage) output.get("OUT_COVERAGE");
//
//		input.setRDFProperty("IN_SOURCE", reference);
//		input.setRDFProperty("IN_TARGET", targetPM10_12);
//		input.setRDFProperty("IN_BAND", new IntegerLiteral(0));
//		input.setRDFProperty("IN_BUFFER", new DecimalLiteral(0.0005)); //~50m in WGS84
//		output = zonalStats.execute(input);
//
//		relations.addAll(((BinaryRelationCollection) output.get("OUT_RELATIONS")).resolve());
//
//		//Output
//		String result;
//		if(tripleStore){
//			TripleStoreGenerator generator = new TripleStoreGenerator();
//			input.setRDFProperty("IN_RDF", relations);
//			input.setRDFProperty("IN_TRIPLE_STORE", new URLLiteral("http://localhost:3030/fusion/update"));
//			input.setRDFProperty("IN_CLEAR_STORE", new BooleanLiteral(true));
//			input.setRDFProperty("IN_URI_PREFIXES", new StringLiteral(""
//					+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/relation#;relation;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#;spatialOp;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#;zonalStats;"
//					+ "http://purl.org/dc/terms/#;dc;"
//					+ "http://purl.org/dc/elements/1.1/#;dc11;"
//					+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
//					+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
//					+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
//			output = generator.execute(input);
//			result = "in tripe store";
//		}
//		else {
//			RDFTurtleGenerator generator = new RDFTurtleGenerator();
//			input.setRDFProperty("IN_RDF", relations);
//			input.setRDFProperty("IN_URI_PREFIXES", new StringLiteral(""
//					+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/relation#;relation;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial#;spatialOp;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#;zonalStats;"
//					+ "http://purl.org/dc/terms/#;dc;"
//					+ "http://purl.org/dc/elements/1.1/#;dc11;"
//					+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
//					+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
//					+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
//			output = generator.execute(input);
//			result = ((URLLiteral) output.get("OUT_RESOURCE")).getLiteralValue();
//		}
//
//		Runtime runtime = Runtime.getRuntime();
//		runtime.gc();
//		System.out.print("TEST: Use Case 1 \n\t" +
//				"number of reference features: " + reference.size() + "\n\t" +
//				"number of identified relations: " + relations.resolve().size() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
//				"result: " + result);
//	}
//
//}
