package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.retrieval.GridCoverageParser;
import de.tudresden.geoinfo.fusion.operation.retrieval.ShapefileParser;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractMeasurementTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
    private final static IIdentifier IN_WITH_INDEX = new Identifier("IN_WITH_INDEX");

    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
    private final static IIdentifier OUT_COVERAGE = new Identifier("OUT_COVERAGE");

    /**
     * read shapefile
     * @param uri shapefile URI
     * @param index flag: read with index
     * @return feature collection from shapefile
     */
    protected GTFeatureCollection readShapefile(URI uri, boolean index) {
        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(uri));
        input.put(IN_WITH_INDEX, new BooleanLiteral(index));
        ShapefileParser parser = new ShapefileParser();
        Map<IIdentifier,IData> output = parser.execute(input);
        return (GTFeatureCollection) output.get(OUT_FEATURES);
    }

    /**
     * read shapefile
     * @param uri grid URI
     * @return grid file
     */
    protected GTGridFeature readGrid(URI uri) {
        Map<IIdentifier,IData> input = new HashMap<>();
        input.put(IN_RESOURCE, new URILiteral(uri));
        GridCoverageParser parser = new GridCoverageParser();
        Map<IIdentifier,IData> output = parser.execute(input);
        return (GTGridFeature) output.get(OUT_COVERAGE);
    }

}
