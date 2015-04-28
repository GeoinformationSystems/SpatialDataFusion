package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.complex.BPMNModel;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.ProcessException;

public class BPMNTest {

	@Test
	public void readBPMNFile() throws ProcessException {
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata", "bpmnXML.xml").toURI()));
		
		BPMNParser parser = new BPMNParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_BPMN"));
		Assert.assertTrue(output.get("OUT_BPMN") instanceof BPMNModel);
		
		BPMNModel model = (BPMNModel) output.get("OUT_BPMN");
		
		Assert.assertTrue(model.getBpmnModelInstance() != null);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + parser.getProfile().getProcessName() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
