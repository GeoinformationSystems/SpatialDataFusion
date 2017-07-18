package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WPSProxyTest extends AbstractTest {

    private final static String WPS_ROOT = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
    private final static String WPS_PROCESS = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";
    private final static String WPS_IN_DATA = "data";
    private final static String WPS_IN_DATA_VALUE = "http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs&MAXFEATURES=1";
    private final static String WPS_IN_WIDTH = "width";
    private final static double WPS_IN_WIDTH_VALUE = 0.1;
    private final static String WPS_OUT_RESULT = "result";

    @Test
    public void executeProcess() {
        executeProcess(
                new URLLiteral(WPS_IN_DATA_VALUE, new IOFormat("text/xml", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", null)),
                new DecimalLiteral(WPS_IN_WIDTH_VALUE));
    }

    private void executeProcess(URLLiteral data, DecimalLiteral width) {

        WPSProxy operation = new WPSProxy(new URLLiteral(WPS_ROOT));
        operation.setProcessId(WPS_PROCESS);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(WPS_IN_DATA, data);
        inputs.put(WPS_IN_WIDTH, width);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(WPS_OUT_RESULT, URLLiteral.class);

        this.execute(operation, inputs, outputs);

    }

}
