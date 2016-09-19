package de.tud.fusion.operation.retrieval.ows;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.literal.LongLiteral;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.data.ows.WFSCapabilities;
import de.tud.fusion.data.ows.WMSCapabilities;
import de.tud.fusion.data.ows.WPSCapabilities;

public class OWSCapabilitiesParserTest {

	@Test
	public void readWMSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("http://localhost:8081/geoserver/fusion/wms?service=WMS&version=1.3.0&request=Getcapabilities")), WMSCapabilities.class);	
	}
	
	@Test
	public void readWFSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("http://localhost:8081/geoserver/fusion/wfs?service=WFS&version=1.1.0&request=Getcapabilities")), WFSCapabilities.class);	
	}
	
	@Test
	public void readWPSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?Request=GetCapabilities&Service=WPS")), WPSCapabilities.class);	
	}

	private void readOWSCapabilities(URILiteral resource, Class<?> type) {
		
		Map<String,IData> input = new HashMap<String,IData>();
		input.put("IN_RESOURCE", resource);
		
		OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
		Map<String,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_CAPABILITIES"));
		Assert.assertTrue(type.isAssignableFrom(output.get("OUT_CAPABILITIES").getClass()));
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getDescription().getIdentifier() + "\n\t" +
				"type: " + type + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get("OUT_RUNTIME")).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
}
