package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.LongLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.io.GMLParser;

public class GMLParserTest {

	@Ignore
	public void readGMLFile_V21() throws ProcessException {
		readGML(new URILiteral(new File("D:/Diss/implementation/testdata/gml", "wfs100.xml").toURI()));	
	}
	
	@Ignore
	public void readGMLFile_V31() throws ProcessException {
		readGML(new URILiteral(new File("D:/Diss/implementation/testdata/gml", "wfs110.xml").toURI()));	
	}
	
	@Ignore
	public void readGMLFile_V32() throws ProcessException {
		readGML(new URILiteral(new File("D:/Diss/implementation/testdata/gml", "wfs20.xml").toURI()));	
	}
	
	@Ignore
	public void readWFS11() throws ProcessException {
		readGML(new URILiteral("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs"));	
	}
	
	@Ignore
	public void readWFS20() throws ProcessException {
		readGML(new URILiteral("http://localhost:8081/geoserver/fusion/wfs?service=WFS&version=2.0&request=GetFeature&typeName=fusion:osm_dd"));	
	}
	
	@Test
	public void readCOBWEB() throws ProcessException {
		readGML(new URILiteral("https://dyfi.cobwebproject.eu/pcapi/ows?version=1.1.0&service=WFS&request=GetFeature&Typename=cobweb:HT_Protokoll_Flat"));	
	}
	
	private void readGML(URILiteral resource) throws ProcessException {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		GMLParser parser = new GMLParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_FEATURES"));
		Assert.assertTrue(output.get("OUT_FEATURES") instanceof GTFeatureCollection);
		
		GTFeatureCollection features = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		Assert.assertTrue(features.size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.profile().processDescription().getTitle() + "\n\t" +
				"features read from gml: " + features.size() + "\n\t" +
				"bounds: " + features.collection().getBounds() + "\n\t" +
				"shape feature crs: " + (features.collection().getSchema().getCoordinateReferenceSystem() != null ? features.collection().getSchema().getCoordinateReferenceSystem().getName() : "not set") + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) parser.output("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}
	
}
