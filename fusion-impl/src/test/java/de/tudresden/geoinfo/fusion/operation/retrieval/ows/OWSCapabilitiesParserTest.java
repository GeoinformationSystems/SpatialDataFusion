package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.OSMXMLParser;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class OWSCapabilitiesParserTest extends AbstractTest {

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

    private void readOWSCapabilities(URLLiteral resource, Class<? extends IData> type) {

        AbstractOperation operation = new OSMXMLParser(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_CAPABILITIES, type);

        this.execute(operation, inputs, outputs);

    }

}
