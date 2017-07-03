package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.retrieval.OSMXMLParser;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WPSDescriptionParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_DESCRIPTION = "OUT_DESCRIPTION";

    @Test
    public void readWPSDescription() throws MalformedURLException {
        readProcessDescription(new URLLiteral(new URL("http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?Request=DescribeProcess&Service=WPS&version=1.0.0&identifier=neighborhooddiversity")));
    }

    private void readProcessDescription(URLLiteral resource) {

        AbstractOperation operation = new OSMXMLParser(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_DESCRIPTION, WPSDescribeProcess.class);

        this.execute(operation, inputs, outputs);

    }

}
