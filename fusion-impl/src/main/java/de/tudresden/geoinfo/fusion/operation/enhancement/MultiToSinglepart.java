package de.tudresden.geoinfo.fusion.operation.enhancement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiToSinglepart extends AbstractOperation {

    private static final String PROCESS_TITLE = MultiToSinglepart.class.getName();
    private static final String PROCESS_DESCRIPTION = "Splits multipart features into singlepart features";

    private final static String IN_FEATURES_TITLE = "IN_FEATURES";
    private final static String IN_FEATURES_DESCRIPTION = "Input features";
    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Singlepart features";

    /**
     * constructor
     */
    public MultiToSinglepart(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        //get input connectors
        IInputConnector featureConnector = getInputConnector(IN_FEATURES_TITLE);
        //get input
        GTFeatureCollection features = (GTFeatureCollection) featureConnector.getData();
        //intersect
        features = multiToSingle(features);
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * computes intersections within a line network
     *
     * @param inFeatures input line features
     * @return intersected line features
     * @throws IOException
     */
    private GTFeatureCollection multiToSingle(GTFeatureCollection inFeatures) {
        //init new collection
        List<SimpleFeature> nFeatures = new ArrayList<>();
        //run intersections
        for (GTVectorFeature feature : inFeatures) {
            if (isMultiGeometry(feature))
                nFeatures.addAll(multiToSingle((SimpleFeature) feature.resolve()));
            else
                nFeatures.add((SimpleFeature) feature.resolve());
        }
        //return
        return new GTFeatureCollection(inFeatures.getIdentifier(), DataUtilities.collection(nFeatures), inFeatures.getMetadata());
    }

    /**
     * transform multi to single geometry
     *
     * @param feature input feature with multi-geometry
     * @return set of single-geometries
     */
    private Collection<? extends SimpleFeature> multiToSingle(SimpleFeature feature) {
        List<SimpleFeature> sfCollection = new ArrayList<>();
        //get geometry
        GeometryCollection geom = (GeometryCollection) feature.getDefaultGeometryProperty().getValue();
        //build new features
        SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeatureType());
        //get feature id
        String fid = feature.getID();
        //iterate and build new single part features
        for (int i = 0; i < geom.getNumGeometries(); i++) {
            sfBuilder.init(feature);
            sfBuilder.set(feature.getDefaultGeometryProperty().getName(), geom.getGeometryN(i));
            sfCollection.add(sfBuilder.buildFeature(fid + "_" + i++));
        }
        return sfCollection;
    }

    /**
     * check, if feature has multi-geometry
     *
     * @param feature input feature
     * @return true, if multi-geometry
     */
    private boolean isMultiGeometry(GTVectorFeature feature) {
        //get default geometry from feature
        Geometry geom = ((GTVectorRepresentation) feature.getRepresentation()).getDefaultGeometry();
        //check, if number of geometries > 1
        return (geom instanceof GeometryCollection);
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_FEATURES_TITLE, IN_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null);
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

}
