//package de.tudresden.gis.fusion.operation.aggregate;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Test;
//
//import de.tudresden.gis.fusion.data.IData;
//import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
//import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
//import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
//import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
//import de.tudresden.gis.fusion.data.literal.LongLiteral;
//import de.tudresden.gis.fusion.data.literal.StringLiteral;
//import de.tudresden.gis.fusion.data.literal.URLLiteral;
//import de.tudresden.gis.fusion.data.relation.BinaryRelationCollection;
//import de.tudresden.gis.fusion.operation.AOperationInstance;
//import de.tudresden.gis.fusion.operation.enhancement.LineIntersection;
//import de.tudresden.gis.fusion.operation.enhancement.MultiToSinglepart;
//import de.tudresden.gis.fusion.operation.io.OSMXMLParser;
//import de.tudresden.gis.fusion.operation.io.RDFTurtleGenerator;
//import de.tudresden.gis.fusion.operation.io.ShapefileParser;
//import de.tudresden.gis.fusion.operation.mapping.BestCorrespondenceMapping;
//import de.tudresden.gis.fusion.operation.measurement.BoundingBoxOverlap;
//import de.tudresden.gis.fusion.operation.measurement.DamerauLevenshteinDistance;
//import de.tudresden.gis.fusion.operation.measurement.HausdorffDistance;
//
//public class UseCase2 {
//
//	private boolean tripleStore = false;
//
//	@Test
//	public void chain() throws MalformedURLException, URISyntaxException {
//
//		ShapefileParser parserShape = new ShapefileParser();
//		OSMXMLParser parserOSM = new OSMXMLParser();
//		BinaryRelationCollection relations;
//
//		Map<String,IData> input = new HashMap<String,IData>();
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_svs_wgs84.shp").toURI()));
//		Map<String,IData> output = parserShape.execute(input);
//		GTFeatureCollection source = (GTFeatureCollection) output.get("OUT_FEATURES");
//
//		input.setRDFProperty("IN_RESOURCE", new URLLiteral(new File("D:/Geodaten/Testdaten/osm", "sample.xml").toURI()));
//		output = parserOSM.execute(input);
//		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_WAYS");
//
//		//singleparts for atkis
//		input.clear();
//		MultiToSinglepart multiTosingle = new MultiToSinglepart();
//		input.setRDFProperty("IN_FEATURES", source);
//		output = multiTosingle.execute(input);
//		source = (GTFeatureCollection) output.get("OUT_FEATURES");
//
//		//intersect osm
//		input.clear();
//		LineIntersection lineIntersect = new LineIntersection();
//		input.setRDFProperty("IN_FEATURES", target);
//		output = lineIntersect.execute(input);
//		target = (GTFeatureCollection) output.get("OUT_FEATURES");
//
//		//bbox measurement
//		input.clear();
//		BoundingBoxOverlap process_bbox = new BoundingBoxOverlap();
//		input.setRDFProperty("IN_SOURCE", source);
//		input.setRDFProperty("IN_TARGET", target);
//		input.setRDFProperty("IN_THRESHOLD", new DecimalLiteral(0.0005));
//		output = process_bbox.execute(input);
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//		systemOut(process_bbox, relations.resolve().size() + " relations");
//
//		//angle difference
//		input.clear();
//		AngleDifference process_angle = new AngleDifference();
//		input.setRDFProperty("IN_SOURCE", source);
//		input.setRDFProperty("IN_TARGET", target);
//		input.setRDFProperty("IN_RELATIONS", relations);
//		input.setRDFProperty("IN_THRESHOLD", new DecimalLiteral(Math.PI/8));
//		input.setRDFProperty("IN_DROP_RELATIONS", new BooleanLiteral(true));
//		output = process_angle.execute(input);
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//		systemOut(process_angle, relations.resolve().size() + " relations");
//
//		//hausdorff distance
//		input.clear();
//		HausdorffDistance process_hd = new HausdorffDistance();
//		input.setRDFProperty("IN_SOURCE", source);
//		input.setRDFProperty("IN_TARGET", target);
//		input.setRDFProperty("IN_RELATIONS", relations);
//		input.setRDFProperty("IN_THRESHOLD", new DecimalLiteral(0.0005));
//		output = process_hd.execute(input);
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//		systemOut(process_hd, relations.resolve().size() + " relations");
//
//		//road names
//		input.clear();
//		DamerauLevenshteinDistance process_dld = new DamerauLevenshteinDistance();
//		input.setRDFProperty("IN_SOURCE", source);
//		input.setRDFProperty("IN_SOURCE_ATT", new StringLiteral("GN"));
//		input.setRDFProperty("IN_TARGET", target);
//		input.setRDFProperty("IN_TARGET_ATT", new StringLiteral("name"));
//		input.setRDFProperty("IN_RELATIONS", relations);
//		input.setRDFProperty("IN_THRESHOLD", new IntegerLiteral(5));
//		output = process_dld.execute(input);
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//		systemOut(process_dld, relations.resolve().size() + " relations");
//
//		//best correspondences
//		input.clear();
//		BestCorrespondenceMapping process_bcm = new BestCorrespondenceMapping();
//		input.setRDFProperty("IN_RELATIONS", relations);
////		input.setRDFProperty("IN_DROP_RELATIONS", new BooleanLiteral(true));
//		output = process_bcm.execute(input);
//		relations = (BinaryRelationCollection) output.get("OUT_RELATIONS");
//		systemOut(process_bcm, relations.resolve().size() + " relations");
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
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/thematic#;thematic;"
//					+ "http://tu-dresden.de/uw/geo/gis/fusion/operation/spatial/raster/zonalStats#;zonalStats;"
//					+ "http://purl.org/dc/terms/#;dc;"
//					+ "http://purl.org/dc/elements/1.1/#;dc11;"
//					+ "http://www.opengis.net/ont/geosparql#;geosparql;"
//					+ "http://www.w3.org/2003/01/geo/wgs84_pos#;geo;"
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
//					+ "http://www.opengis.net/ont/geosparql#;geosparql;"
//					+ "http://www.w3.org/2003/01/geo/wgs84_pos#;geo;"
//					+ "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#;qudt;"
//					+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
//					+ "http://www.w3.org/2001/XMLSchema#;xsd;"));
//			output = generator.execute(input);
//			result = ((URLLiteral) output.get("OUT_RESOURCE")).getLiteralValue();
//		}
//
//		Runtime runtime = Runtime.getRuntime();
//		runtime.gc();
//		System.out.print("TEST: Use Case 2 \n\t" +
//				"number of source features: " + source.size() + "\n\t" +
//				"number of target features: " + target.size() + "\n\t" +
//				"number of identified relations: " + relations.resolve().size() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
//				"result: " + result);
//	}
//
//	private Runtime runtime = Runtime.getRuntime();
//	private void systemOut(AOperationInstance op, String result){
//		runtime.gc();
//		System.out.print(op.profile().getIdentifier() + "\n\t" +
//				"result: " + result + "\n\t" +
//				"process runtime (ms): " + ((LongLiteral) op.output("OUT_RUNTIME")).resolve() + "\n\t" +
//				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
//	}
//
//}
