package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.IObservation;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.observation.ObservationCollection;
import de.tudresden.gis.fusion.data.observation.SpeciesObservation;
import de.tudresden.gis.fusion.operation.ProcessException;

public class GBIFParserTest {

	@Test
	public void readGBIF() throws ProcessException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/Diss/sandbox/GBIF/data/redMilan", "occurrence.txt").toURI()));
		
		GBIFParser parser = new GBIFParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_OBSERVATIONS"));
		Assert.assertTrue(output.get("OUT_OBSERVATIONS") instanceof ObservationCollection);
		
		ObservationCollection features = (ObservationCollection) output.get("OUT_OBSERVATIONS");
		
		Assert.assertTrue(features.resolve().size() > 0);
		
		int withTimeCoords = 0;
		for(IObservation observation : features){
			if(((SpeciesObservation)observation).getMeasurement().getGeometry() != null && observation.getPhenomenonTime() != null)
				withTimeCoords++;
		}
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.profile().processDescription().getTitle() + "\n\t" +
				"observations read from GBIF: " + features.resolve().size() + "\n\t" +
				"observations with timestamp and coordinates: " + withTimeCoords + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
