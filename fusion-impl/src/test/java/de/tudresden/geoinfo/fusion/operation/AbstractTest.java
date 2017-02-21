package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
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
public abstract class AbstractTest {

    private final static String IN_RESOURCE = "IN_RESOURCE";
    private final static String IN_WITH_INDEX = "IN_WITH_INDEX";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_COVERAGE = "OUT_COVERAGE";

    /**
     * read shapefile
     *
     * @param uri   shapefile URI
     * @param index flag: read with index
     * @return feature collection from shapefile
     */
    protected GTFeatureCollection readShapefile(URI uri, boolean index) {

        ShapefileParser parser = new ShapefileParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_IN_WITH_INDEX = parser.getInputConnector(IN_WITH_INDEX).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(OUT_FEATURES).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, new URILiteral(uri));
        input.put(ID_IN_WITH_INDEX, new BooleanLiteral(index));

        Map<IIdentifier, IData> output = parser.execute(input);
        return (GTFeatureCollection) output.get(ID_OUT_FEATURES);
    }

    /**
     * read shapefile
     *
     * @param uri grid URI
     * @return grid file
     */
    protected GTGridFeature readGrid(URI uri) {

        GridCoverageParser parser = new GridCoverageParser();
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_COVERAGE = parser.getOutputConnector(OUT_COVERAGE).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, new URILiteral(uri));

        Map<IIdentifier, IData> output = parser.execute(input);
        return (GTGridFeature) output.get(ID_OUT_COVERAGE);
    }

}
