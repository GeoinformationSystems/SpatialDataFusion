package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class OWSCapabilitiesParserTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
    private final static IIdentifier OUT_CAPABILITIES = new Identifier("OUT_CAPABILITIES");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readWMSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("https://www.umwelt.sachsen.de/umwelt/infosysteme/wms/services/wasser/ueg_utm?service=wms&request=getcapabilities")), WMSCapabilities.class);
	}

	@Test
	public void readWFSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs?service=wfs&request=GetCapabilities")), WFSCapabilities.class);
	}

	@Test
	public void readWPSCapabilities() {
		readOWSCapabilities(new URILiteral(URI.create("http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?Request=GetCapabilities&Service=WPS")), WPSCapabilities.class);
	}

	private void readOWSCapabilities(URILiteral resource, Class<?> type) {

        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_RESOURCE, resource);

		OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
		Map<IIdentifier,IData> output = parser.execute(input);

		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey(OUT_CAPABILITIES));
		Assert.assertTrue(type.isAssignableFrom(output.get(OUT_CAPABILITIES).getClass()));

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"type: " + type + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
