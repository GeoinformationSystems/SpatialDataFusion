package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.Nullable;

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

    GTFeatureParser(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        //get input connectors
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        IInputConnector indexConnector = getInputConnector(IN_WITH_INDEX_TITLE);
        //get data
        URL resourceURL = ((URLLiteral) resourceConnector.getData()).resolve();
        boolean withIndex = ((BooleanLiteral) indexConnector.getData()).resolve();
        //parse features
        GTFeatureCollection features;
        try {
            features = getFeatures(resourceURL, withIndex);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse GML source", e);
        }
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
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
        addInputConnector(null, IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_WITH_INDEX_TITLE, IN_WITH_INDEX_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null);
    }
}
