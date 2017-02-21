package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ShapefileParser extends AbstractOperation {

    private static final String PROCESS_TITLE = ShapefileParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for Esri Shapefile format";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "Shapefile resource";
    private final static String IN_WITH_INDEX_TITLE = "IN_WITH_INDEX";
    private final static String IN_WITH_INDEX_DESCRIPTION = "Flag: create spatial index";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Parsed feature collection";

    /**
     * constructor
     */
    public ShapefileParser() {
        super(null, PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        IInputConnector indexConnector = getInputConnector(IN_WITH_INDEX_TITLE);
        //get data
        URI resourceURI = ((URILiteral) resourceConnector.getData()).resolve();
        boolean withIndex = ((BooleanLiteral) indexConnector.getData()).resolve();
        //parse features
        AbstractFeatureCollection<?> features;
        try {
            features = parseShape(resourceURI.toURL(), withIndex);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse Shapefile", e);
        }
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * parse shapefile
     *
     * @param resourceURL shapefile URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     */
    private AbstractFeatureCollection<?> parseShape(URL resourceURL, boolean withIndex) throws IOException {
        ShapefileDataStore store = new ShapefileDataStore(resourceURL);
        store.setCharset(StandardCharsets.UTF_8);
        String name = store.getTypeNames()[0];
        SimpleFeatureSource source = store.getFeatureSource(name);
        SimpleFeatureCollection shapeFC = DataUtilities.collection(source.getFeatures().features());
        store.dispose();
        IIdentifier identifier = new Identifier(resourceURL.toString());
        if (withIndex)
            return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, shapeFC), null);
        else
            return new GTFeatureCollection(identifier, shapeFC, null);
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URILiteral.class),
                        new MandatoryConstraint()},
                null,
                null);
        addInputConnector(IN_WITH_INDEX_TITLE, IN_WITH_INDEX_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new InputData(new BooleanLiteral(false)).getOutputConnector());
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null);
    }

}
