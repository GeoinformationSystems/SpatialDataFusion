package de.tudresden.geoinfo.fusion.operation.provision;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IDataCollection;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.relation.BinaryRelationCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.mapping.TopologyRelation;
import de.tudresden.geoinfo.fusion.operation.measurement.AngleDifference;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RDFProviderTest extends AbstractTest {

    private final static String IN_TRIPLES = "IN_TRIPLES";
    private final static String IN_URI_PREFIXES = "IN_URI_PREFIXES";
    private final static String OUT_RESOURCE = "OUT_RESOURCE";

    @Test
    public void writeRelations() throws IOException {
        IDataCollection relations = getRelations();
        writeRDF(relations, null);
    }

    @Test
    public void writeRelationsWithMeasurements() throws IOException {
        BinaryRelationCollection relations = getRelations();
        IDataCollection relationsWithMeasurements = getMeasurements(relations);
        writeRDF(relationsWithMeasurements, new StringLiteral(""
					+ "http://tu-dresden.de/uw/geo/gis/fusion,fusion;"
					+ "http://tu-dresden.de/uw/geo/gis/fusion/relation/role,role;"
					+ "http://tu-dresden.de/uw/geo/gis/fusion/relation,relation;"
					+ "http://www.opengis.net/ont/geosparql,geo;"
					+ "http://www.w3.org/1999/02/22-rdf-syntax-ns,rdf;"
					+ "http://www.w3.org/2001/XMLSchema,xs;"));
    }

    @Test
    public void writeMeasurements() throws IOException {
        IDataCollection measurements = getMeasurements(null);
        writeRDF(measurements, null);
    }

    private void writeRDF(IData triples, StringLiteral prefixes) {

        AbstractOperation operation = new RDFTutleProvider();

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_TRIPLES, triples);
        if(prefixes != null)
            inputs.put(IN_URI_PREFIXES, prefixes);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_RESOURCE, URLLiteral.class);

        this.execute(operation, inputs, outputs);

    }

    private BinaryRelationCollection getRelations() throws IOException {

        GTFeatureCollection domainFeatures = ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true);
        GTFeatureCollection rangeFeatures = ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true);

        AbstractOperation operation = new TopologyRelation();

        Map<IIdentifier, IData> operationInputs = new HashMap<>();
        operationInputs.put(operation.getInputConnector("IN_DOMAIN").getIdentifier(), domainFeatures);
        operationInputs.put(operation.getInputConnector("IN_RANGE").getIdentifier(), rangeFeatures);

        Map<IIdentifier,IData> outputs = operation.execute(operationInputs);
        return (BinaryRelationCollection) outputs.get(operation.getOutputConnector("OUT_RELATIONS").getIdentifier());

    }

    private IDataCollection getMeasurements(BinaryRelationCollection relations) throws IOException {

        GTFeatureCollection domainFeatures = ShapefileParser.readShapefile(new File("src/test/resources/lines1.shp").toURI().toURL(), true);
        GTFeatureCollection rangeFeatures = ShapefileParser.readShapefile(new File("src/test/resources/lines2.shp").toURI().toURL(), true);

        AbstractOperation operation = new AngleDifference();

        Map<IIdentifier, IData> operationInputs = new HashMap<>();
        operationInputs.put(operation.getInputConnector("IN_DOMAIN").getIdentifier(), domainFeatures);
        operationInputs.put(operation.getInputConnector("IN_RANGE").getIdentifier(), rangeFeatures);
        if(relations != null)
            operationInputs.put(operation.getInputConnector("IN_RELATIONS").getIdentifier(), relations);

        Map<IIdentifier,IData> outputs = operation.execute(operationInputs);
        if(relations == null)
            return (IDataCollection) outputs.get(operation.getOutputConnector("OUT_MEASUREMENTS").getIdentifier());
        else
            return (IDataCollection) outputs.get(operation.getOutputConnector("OUT_RELATIONS").getIdentifier());

    }

}
