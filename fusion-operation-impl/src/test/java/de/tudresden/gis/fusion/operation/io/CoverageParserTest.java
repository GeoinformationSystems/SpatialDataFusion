package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTGridCoverage;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.io.GridCoverageParser;

public class CoverageParserTest {

	@Test
	public void readCoverage() throws ProcessException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/implementation/testdata/tif", "dem.tif").toURI()));
		
		GridCoverageParser parser = new GridCoverageParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_COVERAGE"));
		Assert.assertTrue(output.get("OUT_COVERAGE") instanceof GTGridCoverage);
		
		GTGridCoverage coverage = (GTGridCoverage) output.get("OUT_COVERAGE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.profile().processDescription().getTitle() + "\n\t" +
				"bounds: " + coverage.resolve().getEnvelope() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
