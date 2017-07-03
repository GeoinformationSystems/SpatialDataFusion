package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GMLParserTest extends AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";

    private final static String OUT_FEATURES = "OUT_FEATURES";

    @Test
    public void readWFS100() throws MalformedURLException {
        readGML(new URLLiteral(new URL("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs?service=wfs&version=1.0.0&request=getfeature&typename=gk:waterlevels")));
    }

    @Test
    public void readWFS110() throws MalformedURLException {
        readGML(new URLLiteral(new URL("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs?service=wfs&version=1.1.0&request=getfeature&typename=gk:waterlevels")));
    }

    private void readGML(URLLiteral resource) {

        AbstractOperation operation = new GMLParser(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_RESOURCE, resource);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);

        this.execute(operation, inputs, outputs);

    }

}
