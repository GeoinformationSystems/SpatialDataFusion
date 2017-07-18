package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;

import java.io.IOException;
import java.net.URL;

/**
 *
 */
public abstract class GTFeatureParser extends AbstractOperation {

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "Feature resource";
    private final static String IN_WITH_INDEX_TITLE = "IN_WITH_INDEX";
    private final static String IN_WITH_INDEX_DESCRIPTION = "Flag: create spatial index";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Parsed feature collection";

    GTFeatureParser(String title, String description) {
        super(title, description);
    }

    @Override
    public void executeOperation() {

        //get data
        URL resourceURL = ((URLLiteral) this.getMandatoryInputData(IN_RESOURCE_TITLE)).resolve();
        boolean withIndex = ((BooleanLiteral)  this.getMandatoryInputData(IN_WITH_INDEX_TITLE)).resolve();

        //parse features
        GTFeatureCollection features;
        try {
            features = getFeatures(resourceURL, withIndex);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse GML source", e);
        }

        //set output connector
        setOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * parse GML from URL
     *
     * @param resourceURL input URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     */
    public abstract GTFeatureCollection getFeatures(URL resourceURL, boolean withIndex) throws IOException;

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_WITH_INDEX_TITLE, IN_WITH_INDEX_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null);
    }
}
