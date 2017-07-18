package de.tudresden.geoinfo.fusion.operation.enhancement;

import com.vividsolutions.jts.geom.*;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.data.DataUtilities;
import org.jetbrains.annotations.NotNull;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResampleGeometry extends AbstractOperation {

    private static final String PROCESS_TITLE = ResampleGeometry.class.getName();
    private static final String PROCESS_DESCRIPTION = "Resampling of linear geometries based on provided spatial interval";

    private final static String IN_FEATURES_TITLE = "IN_FEATURES";
    private final static String IN_FEATURES_DESCRIPTION = "Input features";
    private final static String IN_INTERVAL_TITLE = "IN_INTERVAL";
    private final static String IN_INTERVAL_DESCRIPTION = "Resampling interval";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Resampled features";

    /**
     * constructor
     */
    public ResampleGeometry() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {
        //get input
        GTFeatureCollection features = (GTFeatureCollection) this.getMandatoryInputData(IN_FEATURES_TITLE);
        DecimalLiteral interval = (DecimalLiteral) this.getMandatoryInputData(IN_INTERVAL_TITLE);
        //resample
        features = resampleGeometry(features, interval.resolve());
        //set output connector
        setOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * computes resampling
     *
     * @param inFeatures input line features
     * @return resampled line features
     */
    private @NotNull GTFeatureCollection resampleGeometry(@NotNull GTFeatureCollection inFeatures, double interval) {
        //init new collection
        List<SimpleFeature> nFeatures = new ArrayList<>();
        //resample feature geometries
        for (GTVectorFeature feature : inFeatures) {
            if(feature.getRepresentation() != null)
                nFeatures.add(resampleGeometry(feature.getRepresentation(), interval));
        }
        return new GTFeatureCollection(inFeatures.getIdentifier(), DataUtilities.collection(nFeatures), inFeatures.getMetadata());
    }

    /**
     * computes resampling for specified feature
     *
     * @param featureRepresentation input line feature representation
     * @return resampled line features
     */
    private @NotNull SimpleFeature resampleGeometry(@NotNull IFeatureRepresentation featureRepresentation, double interval) {
        return resampleGeometry((SimpleFeature) featureRepresentation.resolve(), interval);
    }

    /**
     * computes resampling for input feature
     *
     * @param feature     input feature
     * @return resampled line features
     */
    private @NotNull SimpleFeature resampleGeometry(@NotNull SimpleFeature feature, double interval) {
        Geometry geometry = Utilities.getGeometry(feature, new BindingConstraint(LineString.class, Polygon.class), true);
        if (geometry == null)
            return feature;
        Geometry resampledGeometry = resampleGeometry(geometry, interval);
        feature.setDefaultGeometry(resampledGeometry);
        return feature;
    }

    /**
     * resample geometry
     * @param geometry input geometry
     * @param interval resampling interval
     * @return resampled geometry
     */
    public static Geometry resampleGeometry(Geometry geometry, double interval) {
        LinkedList<Coordinate> list = new LinkedList<>();
        double initialDistance = interval;
        Coordinate start = null, end;
        boolean first = true;
        for(Coordinate coord : geometry.getCoordinates()){
            if(first){
                // add first coordinate
                list.add(coord);
                // set first coordinate as start
                start = coord;
                first = false;
            }
            else {
                //set current coordinate as end
                end = coord;
                //get distance start - end
                double distanceStartEnd = start.distance(end);
                if(initialDistance < distanceStartEnd){
                    //first split at initialDistance
                    Coordinate split = splitLine(start, end, initialDistance);
                    list.add((Coordinate) split.clone());
                    //set last split to start
                    start = split;
                    //check rest
                    double rest = distanceStartEnd - initialDistance;
                    if(rest == interval){
                        //directly add coordinate
                        list.add(coord);
                        //set rest distance to 0
                        rest = 0;
                    }
                    //further split until rest is lower than interval
                    while(rest > interval){
                        split = splitLine(start, end, interval);
                        list.add((Coordinate) split.clone());
                        //set last split to start
                        start = split;
                        //reduce rest
                        rest = rest - interval;
                    }
                    //set initial distance to interval - rest
                    initialDistance = interval - rest;
                }
                else if(initialDistance > distanceStartEnd){
                    //set intervalDistance to initialDistance - distanceStartEnd
                    initialDistance = initialDistance - distanceStartEnd;
                }
                else {
                    //directly add coordinate
                    list.add(coord);
                    //set interval distance to 0
                    initialDistance = 0;
                }
                //set current coordinate as start
                start = coord;
            }
        }
        //return new geometry
        Coordinate[] coordinates = list.toArray(new Coordinate[list.size()]);
        GeometryFactory factory = new GeometryFactory();
        if(geometry instanceof Polygon)
            return factory.createPolygon(coordinates);
        else
            return factory.createLineString(coordinates);
    }

    /**
     * split line
     * @param start start coordinate
     * @param end end coordinate
     * @param distance split distance
     * @return coordinate of split point
     */
    public static Coordinate splitLine(Coordinate start, Coordinate end, double distance) {
        //get ratio interval / total length
        double ratio = distance / start.distance(end);
        //create coordinate based on the ratio
        return new Coordinate(start.x + ratio * (end.x - start.x), start.y + ratio * (end.y - start.y));
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_FEATURES_TITLE, IN_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_INTERVAL_TITLE, IN_INTERVAL_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
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
