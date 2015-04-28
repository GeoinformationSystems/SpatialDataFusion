package de.tudresden.gis.fusion.operation.aggregate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.complex.BPMNModel;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.retrieval.BPMNParser;

public class BPMNAggregateTest {

	@Test
	public void aggregateRelations() {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", new URILiteral(new File("D:/GIS/Programmierung/testdata", "bpmnXML.xml").toURI()));
		BPMNParser parser = new BPMNParser();
		Map<String,IData> output = parser.execute(input);
		BPMNModel model = (BPMNModel) output.get("OUT_BPMN");
		
		BPMNAggregate process = new BPMNAggregate();
		input.put("IN_BPMN", model);
		output = process.execute(input);
		
		StringLiteral result = (StringLiteral) output.get("OUT_RESULT");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProfile().getProcessName() + "\n\t" +
				"output: " + result.getValue() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
		
	}
	
}
