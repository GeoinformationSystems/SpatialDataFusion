package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class OWSCapabilitiesParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_CAPABILITIES = "OUT_CAPABILITIES";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readWMSCapabilities() throws MalformedURLException {
        readOWSCapabilities(new URLLiteral("https://www.umwelt.sachsen.de/umwelt/infosysteme/wms/services/wasser/ueg_utm?service=wms&request=getcapabilities"), WMSCapabilities.class);
    }

    @Test
    public void readWFSCapabilities() throws MalformedURLException {
        readOWSCapabilities(new URLLiteral("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs?service=wfs&request=GetCapabilities"), WFSCapabilities.class);
    }

    @Test
    public void readWPSCapabilities() throws MalformedURLException {
        readOWSCapabilities(new URLLiteral("http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?Request=GetCapabilities&Service=WPS"), WPSCapabilities.class);
    }

    private void readOWSCapabilities(URLLiteral resource, Class<?> type) {

        OWSCapabilitiesParser parser = new OWSCapabilitiesParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_CAPABILITIES = parser.getOutputConnector(OUT_CAPABILITIES).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, resource);

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_CAPABILITIES));
        Assert.assertTrue(type.isAssignableFrom(output.get(ID_OUT_CAPABILITIES).getClass()));

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "type: " + type + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
