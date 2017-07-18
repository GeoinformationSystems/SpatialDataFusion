package de.tudresden.geoinfo.fusion.operation.enhancement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

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
    public MultiToSinglepart() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {
        //get input
        GTFeatureCollection features = (GTFeatureCollection) this.getMandatoryInputData(IN_FEATURES_TITLE);
        //intersect
        features = multiToSingle(features);
        //set output connector
        setOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * computes intersections within a line network
     *
     * @param inFeatures input line features
     * @return intersected line features
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
        Geometry geom = Utilities.getGeometry(feature);
        //check, if number of geometries > 1
        return (geom instanceof GeometryCollection);
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
        addOutputConnector(OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null);
    }

}
