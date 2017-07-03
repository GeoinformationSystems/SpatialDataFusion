package de.tudresden.geoinfo.fusion.operation.provision;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class RDFProviderTest extends AbstractTest {

    private final static String IN_TRIPLES = "IN_TRIPLES";

    private final static String OUT_RESOURCE = "OUT_RESOURCE";

    @Test
    public void writeRelations() throws MalformedURLException {

//        writeRDF();
    }

    @Test
    public void writeMeasurements() throws MalformedURLException {
//        writeRDF();
    }

    private void writeRDF(IData triples) {

        AbstractOperation operation = new RDFProvider(null);

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_TRIPLES, triples);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_RESOURCE, URLLiteral.class);

        this.execute(operation, inputs, outputs);

    }

}
