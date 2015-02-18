package de.tudresden.gis.fusion.operation.relation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.provision.RDFTurtleGenerator;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;
import de.tudresden.gis.fusion.operation.retrieval.GridCoverageParser;

public class ZonalStatisticsTest {
	
	@Test
	public void calculateStats() throws IOException {
	
	Map<String,IData> input = new HashMap<String,IData>();
	Map<String,IData> output;
	
	GMLParser featureParser = new GMLParser();
	input.put("IN_GML_RESOURCE", new Resource(new IRI("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=fusion:municipalities&srsname=crs:84")));
	output = featureParser.execute(input);		
	IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
	
	String request = "http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__dem";
	input.put("IN_COVERAGE_RESOURCE", new Resource(new IRI(request)));
	GridCoverageParser coverageParser = new GridCoverageParser();
	output = coverageParser.execute(input);
	ICoverage target = (ICoverage) output.get("OUT_COVERAGE");
	
//	input.put("IN_COVERAGE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "dem.tif").toURI())));
//	GridCoverageParser coverageParser = new GridCoverageParser();
//	output = coverageParser.execute(input);
//	GTGridCoverage2D target = (GTGridCoverage2D) output.get("OUT_COVERAGE");
	
	//transform coordinates (workaround to avoid different encoding for same crs)
//	CoordinateReferenceSystem finalCRS = ((GTFeatureCollection) reference).getSimpleFeatureCollection().getSchema().getCoordinateReferenceSystem();
//	GridCoverage2D targetTransformed = new GridCoverage2D(null, (GridCoverage2D) Operations.DEFAULT.resample(((GTGridCoverage2D) target).getCoverage(), finalCRS));
//	ICoverage targetCoverage = new GTGridCoverage2D(target.getIdentifier(), targetTransformed);
	
	ZonalStatistics process = new ZonalStatistics();
	
	input.put("IN_REFERENCE", reference);
	input.put("IN_TARGET", target);
	input.put("IN_BAND", new IntegerLiteral(0));
	output = process.execute(input);	
	IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
	
	//write triples
	RDFTurtleGenerator generator = new RDFTurtleGenerator();
	input.put("IN_DATA", relations);
	input.put("URI_PREFIXES", new StringLiteral(""
			+ "http://tu-dresden.de/uw/geo/gis/fusion#;fusion;"
			+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#;rdf;"
			+ "http://www.w3.org/2001/XMLSchema#;xsd;"
			+ "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#;demo;"
			+ "http://tu-dresden.de/uw/geo/gis/fusion/relation/statistics#;statistics"));
	output = generator.execute(input);	
	IDataResource file = (IDataResource) output.get("OUT_FILE");
	
	Runtime runtime = Runtime.getRuntime();
	runtime.gc();
	System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
			"number of reference features: " + reference.size() + "\n\t" +
			"number of identified relations: " + relations.size() + "\n\t" +
			"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
			"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
			"target relation file: " + file.getIdentifier().asString() + "\n");	
	}

}
