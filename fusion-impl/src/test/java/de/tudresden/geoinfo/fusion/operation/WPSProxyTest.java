package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescription;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescriptions;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.WPSDescriptionParser;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WPSProxyTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String OUT_DESCRIPTION = "OUT_DESCRIPTION";

    private final static String WPS_ROOT = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
    private final static String WPS_PROCESS = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";
    private final static String WPS_IN_DATA = "data";
    private final static String WPS_IN_DATA_VALUE = "http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs&MAXFEATURES=1";
    private final static String WPS_IN_WIDTH = "width";
    private final static double WPS_IN_WIDTH_VALUE = 1;
    private final static String WPS_OUT_RESULT = "result";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void executeProcess() {
        WPSProcessDescription processDescription = getProcessDescription(WPS_ROOT, WPS_PROCESS);
        WPSProxyOperation process = new WPSProxyOperation(processDescription);

        IIdentifier ID_IN_DATA = process.getInputConnector(WPS_IN_DATA).getIdentifier();
        IIdentifier ID_IN_WIDTH = process.getInputConnector(WPS_IN_WIDTH).getIdentifier();
        IIdentifier ID_OUT_RESULT = process.getOutputConnector(WPS_OUT_RESULT).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = process.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_DATA, new URILiteral(URI.create(WPS_IN_DATA_VALUE)));
        input.put(ID_IN_WIDTH, new DecimalLiteral(WPS_IN_WIDTH_VALUE));

        Map<IIdentifier, IData> output = process.execute(input);

//        Assert.assertNotNull(output);
//        Assert.assertTrue(output.containsKey(ID_OUT_RESULT));
//        Assert.assertTrue(output.get(ID_OUT_RESULT) instanceof URILiteral);
//
//        URILiteral result = (URILiteral) output.get(ID_OUT_RESULT);
//
//        Runtime runtime = Runtime.getRuntime();
//        runtime.gc();
//        System.out.print("TEST: " + process.getIdentifier() + "\n\t" +
//                "result of the process: " + result.resolve().toString() + "\n\t" +
//                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
//                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");

    }

    private WPSProcessDescription getProcessDescription(String wpsURI, String wpsProcess) {
        WPSDescriptionParser parser = new WPSDescriptionParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_DESCRIPTION = parser.getOutputConnector(OUT_DESCRIPTION).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, new URILiteral(URI.create(wpsURI + "?Request=DescribeProcess&Service=WPS&version=1.0.0&identifier=" + wpsProcess)));

        Map<IIdentifier, IData> output = parser.execute(input);
        return ((WPSProcessDescriptions) output.get(ID_OUT_DESCRIPTION)).getProcessDescription();
    }

}
