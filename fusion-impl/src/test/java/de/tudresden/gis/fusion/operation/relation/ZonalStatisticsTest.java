package de.tudresden.gis.fusion.operation.relation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.retrieval.GMLParser;
import de.tudresden.gis.fusion.operation.retrieval.GridCoverageParser;

public class ZonalStatisticsTest {
	
	@Test
	public void calculateStats() throws IOException {
	
	Map<String,IData> input = new HashMap<String,IData>();
	Map<String,IData> output;
	
	GMLParser featureParser = new GMLParser();
	input.put("IN_RESOURCE", new URILiteral("http://localhost:8081/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=fusion:municipalities&srsname=crs:84"));
	output = featureParser.execute(input);		
	IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");
	
	String request = "http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__dem";
	input.put("IN_RESOURCE", new URILiteral(request));
	GridCoverageParser coverageParser = new GridCoverageParser();
	output = coverageParser.execute(input);
	ICoverage target = (ICoverage) output.get("OUT_COVERAGE");
	
	ZonalStatistics process = new ZonalStatistics();
	
	input.put("IN_REFERENCE", reference);
	input.put("IN_TARGET", target);
	input.put("IN_BAND", new IntegerLiteral(0));
	output = process.execute(input);	
	IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
	
	Runtime runtime = Runtime.getRuntime();
	runtime.gc();
	System.out.print("executing " + process.getProfile().getProcessName() + "\n\t" +
			"number of reference features: " + reference.size() + "\n\t" +
			"number of identified relations: " + relations.size() + "\n\t" +
			"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
			"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)));	
	}

}
