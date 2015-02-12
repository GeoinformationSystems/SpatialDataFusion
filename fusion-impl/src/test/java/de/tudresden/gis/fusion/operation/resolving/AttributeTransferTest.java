package de.tudresden.gis.fusion.operation.resolving;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.relation.TopologyRelation;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class AttributeTransferTest {

	@Test
	public void transfer() {
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		ShapefileParser parser = new ShapefileParser();
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_highDensity.shp").toURI())));
		Map<String,IData> output = parser.execute(input);		
		IFeatureCollection reference = (IFeatureCollection) output.get("OUT_FEATURES");		
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "osm_highDensity.shp").toURI())));
		output = parser.execute(input);		
		IFeatureCollection target = (IFeatureCollection) output.get("OUT_FEATURES");
		
		TopologyRelation process = new TopologyRelation();		
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		output = process.execute(input);	
		IFeatureRelationCollection relations = (IFeatureRelationCollection) output.get("OUT_RELATIONS");
		
		AttributeTransfer transferProcess = new AttributeTransfer();
		input.put("IN_REFERENCE", reference);
		input.put("IN_TARGET", target);
		input.put("IN_TARGET_ATT", new StringLiteral("[Steetname,oldID]"));
		input.put("IN_RELATIONS", relations);
		input.put("IN_TARGET_RELATION", new RelationType(new IRI("http://tu-dresden.de/uw/geo/gis/fusion/similarity/topology#de-9im")));
		output = transferProcess.execute(input);	
		IFeatureCollection features = (IFeatureCollection) output.get("OUT_REFERENCE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + transferProcess.getProcessIRI().asString() + "\n\t" +
				"number of reference features: " + reference.size() + "\n\t" + 
				"number of target features: " + target.size() + "\n\t" +
				"number of output features with transferred attributes: " + features.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
}
