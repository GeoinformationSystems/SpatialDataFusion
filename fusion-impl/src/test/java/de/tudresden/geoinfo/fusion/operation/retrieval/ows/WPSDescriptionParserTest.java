package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WPSDescriptionParserTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_DESCRIPTION = "OUT_DESCRIPTION";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readWPSDescription() throws MalformedURLException {

        WPSDescriptionParser parser = new WPSDescriptionParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_DESCRIPTION = parser.getOutputConnector(OUT_DESCRIPTION).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, new URLLiteral(new URL("http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?Request=DescribeProcess&Service=WPS&version=1.0.0&identifier=neighborhooddiversity")));

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_DESCRIPTION));
        Assert.assertTrue(output.get(ID_OUT_DESCRIPTION) instanceof WPSDescribeProcess);

        WPSDescribeProcess description = (WPSDescribeProcess) output.get(ID_OUT_DESCRIPTION);
        Assert.assertTrue(description.getProcessIdentifier().size() == 1);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
