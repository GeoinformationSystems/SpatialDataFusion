package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ShapefileParser extends GTFeatureParser {

    private static final String PROCESS_TITLE = ShapefileParser.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for Esri Shapefile format";

    /**
     * constructor
     */
    public ShapefileParser() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    /**
     * parse shapefile
     *
     * @param resourceURL shapefile URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     */
    public GTFeatureCollection getFeatures(URL resourceURL, boolean withIndex) throws IOException {
        ShapefileDataStore store = new ShapefileDataStore(resourceURL);
        store.setCharset(StandardCharsets.UTF_8);
        String name = store.getTypeNames()[0];
        SimpleFeatureSource source = store.getFeatureSource(name);
        SimpleFeatureCollection shapeFC = DataUtilities.collection(source.getFeatures().features());
        store.dispose();
        IIdentifier identifier = new ResourceIdentifier(resourceURL.toString());
        if (withIndex)
            return new GTIndexedFeatureCollection(identifier, GTFeatureCollection.getGTCollection(identifier, shapeFC), null);
        else
            return new GTFeatureCollection(identifier, shapeFC, null);
    }

    /**
     * read shapefile
     *
     * @param url   shapefile URL
     * @param index flag: read with index
     * @return feature collection from shapefile
     */
    public static GTFeatureCollection readShapefile(URL url, boolean index) throws IOException {
        return new ShapefileParser().getFeatures(url, index);
    }

}
