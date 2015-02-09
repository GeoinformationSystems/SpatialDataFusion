package de.tudresden.gis.fusion.operation.provision;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.misc.OSMCollection;
import de.tudresden.gis.fusion.operation.retrieval.OSMParser;

public class ShapefileGeneratorTest {

	@Test
	public void writeOSMToShape() throws MalformedURLException, URISyntaxException {
		
		//set extent
		String extent = "13.769,51.044,13.806,51.058";
//		String extent = "13.769,51.044,13.780,51.050";
		
		String url = "http://open.mapquestapi.com/xapi/api/0.6/way%5Bbbox=" + extent + "%5D";
		
		//get features from OSM
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_OSM_URL", new Resource(new IRI(url)));
		
		OSMParser parser = new OSMParser();
		Map<String,IData> output = parser.execute(input);
		
		OSMFeatureCollection osmData = (OSMFeatureCollection) output.get("OUT_OSM_COLLECTION");
		OSMCollection osmFC = osmData.getOSMCollection();

		SimpleFeatureCollection ways = osmFC.getWays();
		
		//write shapefile
		ShapefileGenerator generator = new ShapefileGenerator();
		input.put("IN_FEATURES", new GTFeatureCollection(osmData.getIdentifier(), ways));
		
		output = generator.execute(input);
		IDataResource file = (IDataResource) output.get("OUT_FILE");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + generator.getProcessIRI().asString() + "\n\t" +
				"number of features: " + ways.size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) generator.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n\t" +
				"target shapefile: " + file.getIdentifier().asString() + "\n");
	}
	
}
