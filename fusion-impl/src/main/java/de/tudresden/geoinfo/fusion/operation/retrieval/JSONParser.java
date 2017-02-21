package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
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
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class JSONParser extends AbstractOperation {

    private static final String PROCESS_TITLE = JSONParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for GeoJSON format";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "GeoJSON resource";
    private final static String IN_WITH_INDEX_TITLE = "IN_WITH_INDEX";
    private final static String IN_WITH_INDEX_DESCRIPTION = "Flag: create spatial index";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Parsed feature collection";

    /**
     * constructor
     */
    public JSONParser() {
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
        GTFeatureCollection features;
        try {
            features = parseJSON(resourceURI.toURL(), withIndex);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse JSON source", e);
        }
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * parse JSON from URL
     *
     * @param resourceURL input URL
     * @return GeoTools feature collection
     * @throws IOException
     */
    private GTFeatureCollection parseJSON(URL resourceURL, boolean withIndex) throws IOException {
        //parse HTTP connection
        if (resourceURL.getProtocol().toLowerCase().startsWith("http"))
            return parseJSONFromHTTP(resourceURL, withIndex);
            //parse file
        else if (resourceURL.getProtocol().toLowerCase().startsWith("file"))
            return parseJSONFromFile(resourceURL, withIndex);
            //else: throw IOException
        else
            throw new IOException("Unsupported JSON source: " + resourceURL.toString());
    }

    /**
     * parse JSON from HTTP
     *
     * @param resourceURL input URL (set as identifier)
     * @return GeoTools feature collection
     * @throws IOException
     */
    private GTFeatureCollection parseJSONFromHTTP(URL resourceURL, boolean withIndex) throws IOException {
        //get connection
        HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
        urlConnection.connect();
        return parseJSON(resourceURL, urlConnection.getInputStream(), withIndex);
    }

    /**
     * parse JSON from file
     *
     * @param resourceURL input URL (set as identifier)
     * @return GeoTools feature collection
     * @throws IOException
     */
    private GTFeatureCollection parseJSONFromFile(URL resourceURL, boolean withIndex) throws IOException {
        File file = new File(resourceURL.getFile());
        if (!file.exists() || file.isDirectory())
            return null;
        //redirect based on content type
        return parseJSON(resourceURL, new FileInputStream(file), withIndex);
    }

    /**
     * parse JSON feature collection using GeoTools
     *
     * @param resourceURL input URL (set as identifier)
     * @param stream      input JSON stream
     * @return GeoTools feature collection
     * @throws IOException
     */
    private GTFeatureCollection parseJSON(URL resourceURL, InputStream stream, boolean withIndex) throws IOException {
        FeatureJSON io = new FeatureJSON();
        DefaultFeatureCollection collection = (DefaultFeatureCollection) io.readFeatureCollection(stream);
        IIdentifier identifier = new Identifier(resourceURL.toString());
        if (withIndex)
            return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, collection), null);
        else
            return new GTFeatureCollection(identifier, collection, null);
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
