package de.tudresden.geoinfo.fusion.operation.provision;

import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.geojson.feature.FeatureJSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class JSONProvider extends AbstractOperation {

    private static final String PROCESS_TITLE = JSONProvider.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Generator for GeoJSON format";

    private final static String IN_FEATURES_TITLE = "IN_FEATURES";
    private final static String IN_FEATURES_DESCRIPTION = "Input features";

    private final static String OUT_RESOURCE_TITLE = "OUT_RESOURCE";
    private final static String OUT_RESOURCE_DESCRIPTION = "Link to JSON encoded file";

    public JSONProvider() {
        super(null, PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector dataConnector = getInputConnector(IN_FEATURES_TITLE);
        //get data
        GTFeatureCollection features = (GTFeatureCollection) dataConnector.getData();
        //init result
        URLLiteral jsonResource;
        //check for triple store URI
        try {
            jsonResource = encodeFeatures(features);
        } catch (IOException e) {
            throw new RuntimeException("Could not access or write output features", e);
        }
        //set output connector
        connectOutput(OUT_RESOURCE_TITLE, jsonResource);
    }

    /**
     * write feature data to file
     *
     * @param features input features
     * @return URI to generated file
     * @throws IOException
     */
    private URLLiteral encodeFeatures(GTFeatureCollection features) throws IOException {
        //init file
        File file = File.createTempFile("json_" + UUID.randomUUID(), ".rdf");
        //write RDF turtles
        writeFeaturesToFile(features, file);
        //return
        return new URLLiteral(file.toURI().toURL());
    }

    /**
     * write feature data to file
     *
     * @param features input features
     * @param file     output file
     * @throws IOException if triples cannot be written to file
     */
    private void writeFeaturesToFile(GTFeatureCollection features, File file) throws IOException {
        //create file writer
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            //write features
            writer.append(new FeatureJSON().toString(features.collection()));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_FEATURES_TITLE, IN_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_RESOURCE_TITLE, OUT_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class)},
                null);
    }

}
