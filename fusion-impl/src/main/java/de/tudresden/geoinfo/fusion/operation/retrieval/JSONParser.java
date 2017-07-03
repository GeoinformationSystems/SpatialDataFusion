package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser extends GTFeatureParser {

    private static final String PROCESS_TITLE = JSONParser.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for GeoJSON format";

    /**
     * constructor
     */
    public JSONParser(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public GTFeatureCollection getFeatures(URL resourceURL, boolean withIndex) throws IOException {
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
        FeatureCollection collection = io.readFeatureCollection(stream);
        IIdentifier identifier = new Identifier(resourceURL.toString());
        if (withIndex)
            return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, collection), null);
        else
            return new GTFeatureCollection(identifier, collection, null);
    }

    @NotNull
    @Override
    public String getTitle() {
        return PROCESS_TITLE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PROCESS_DESCRIPTION;
    }

    /**
     * read json
     *
     * @param url   json URL
     * @param index flag: read with index
     * @return feature collection from json
     */
    public static GTFeatureCollection readJSON(URL url, boolean index) throws IOException {
        return new JSONParser(null).getFeatures(url, index);
    }

}
