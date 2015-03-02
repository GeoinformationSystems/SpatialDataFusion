package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;

public class CoverageTest {

	@Test
	public void readCoverage() throws MalformedURLException, URISyntaxException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		
//		String request = "http://localhost:8081/geoserver/wcs?request=getcoverage&version=2.0.0&coverageId=fusion__dem";
//		input.put("IN_COVERAGE_RESOURCE", new Resource(new IRI(request)));
		
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata/fusion_test", "dem.tif").toURI()));
		
		GridCoverageParser parser = new GridCoverageParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_COVERAGE"));
		Assert.assertTrue(output.get("OUT_COVERAGE") instanceof ICoverage);
		
		ICoverage resource = (ICoverage) output.get("OUT_COVERAGE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + parser.getProfile().getProcessName() + "\n\t" +
				"image instance of: " + resource.getClass() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");		
	}
	
}
