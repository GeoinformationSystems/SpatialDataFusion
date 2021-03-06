package de.tudresden.geoinfo.fusion.operation.enhancement;

import com.vividsolutions.jts.geom.*;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.jetbrains.annotations.NotNull;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

public class LineIntersection extends AbstractOperation {

    private static final String PROCESS_TITLE = LineIntersection.class.getName();
    private static final String PROCESS_DESCRIPTION = "Intersects linear network to avoid topological inconsistency";

    private final static String IN_FEATURES_TITLE = "IN_FEATURES";
    private final static String IN_FEATURES_DESCRIPTION = "Input features";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Intersected features";

    /**
     * constructor
     */
    public LineIntersection() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {
        //get input
        GTFeatureCollection features = (GTFeatureCollection) this.getMandatoryInputData(IN_FEATURES_TITLE);
        //intersect
        features = runIntersection(features);
        //set output connector
        setOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * computes intersections within a line network
     *
     * @param inFeatures input line features
     * @return intersected line features
     */
    private GTFeatureCollection runIntersection(@NotNull GTFeatureCollection inFeatures) {
        //build index
        GTIndexedFeatureCollection fc = new GTIndexedFeatureCollection(inFeatures.getIdentifier(), inFeatures.resolve(), inFeatures.getMetadata());
        //init new collection
        List<SimpleFeature> nFeatures = new ArrayList<>();
        //run intersections
        for (GTVectorFeature feature : inFeatures) {
            nFeatures.addAll(runIntersection(feature, fc));
        }
        //return
        return new GTFeatureCollection(inFeatures.getIdentifier(), DataUtilities.collection(nFeatures), inFeatures.getMetadata());
    }

    /**
     * computes intersections for specified feature
     *
     * @param feature input line feature
     * @param fc      intersection features
     * @return intersected line features
     */
    private @NotNull List<SimpleFeature> runIntersection(@NotNull GTVectorFeature feature, @NotNull GTIndexedFeatureCollection fc) {
        //get potential intersections
        if(feature.getRepresentation() == null)
            return Collections.emptyList();
        List<GTVectorFeature> pIntersects = fc.boundsIntersect(((GTVectorRepresentation) feature.getRepresentation()).resolve());
        //get intersections
        return runIntersection((SimpleFeature) feature.getRepresentation().resolve(), pIntersects);
    }

    /**
     * computes intersections for input feature
     *
     * @param feature     input feature
     * @param pIntersects possibly intersecting features
     * @return intersected line features
     */
    private List<SimpleFeature> runIntersection(@NotNull SimpleFeature feature, @NotNull List<GTVectorFeature> pIntersects) {
        List<SimpleFeature> sfCollection = new ArrayList<>();
        //get geometry and intersections
        Geometry refGeometry = Utilities.getGeometry(feature, new BindingConstraint(LineString.class, Polygon.class), true);
        if (refGeometry == null)
            return sfCollection;
        List<Geometry> intersectingGeometries = new ArrayList<>();
        for (GTVectorFeature pIntersect : pIntersects) {
            Geometry tarGeometry = Utilities.getGeometry(pIntersect, new BindingConstraint(LineString.class, Polygon.class), true);
            //continue, if lines do not intersect or lines are equal
            if (tarGeometry == null || !tarGeometry.intersects(refGeometry) || tarGeometry.equals(refGeometry))
                continue;
            intersectingGeometries.add(tarGeometry);
        }
        //check if intersections are present
        if (intersectingGeometries.size() == 0) {
            sfCollection.add(feature);
            return sfCollection;
        }
        //intersect feature
        List<LineString> nGeometries = runIntersection(refGeometry, intersectingGeometries);
        //build new features
        SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(feature.getFeatureType());
        //get feature id
        String fid = feature.getID();
        //iterate segments and build new features
        int i = 0;
        for (Geometry geom : nGeometries) {
            sfBuilder.init(feature);
            sfBuilder.set(feature.getDefaultGeometryProperty().getName(), geom);
            sfCollection.add(sfBuilder.buildFeature(fid + "_" + i++));
        }
        return sfCollection;
    }

    /**
     * computes line intersection
     *
     * @param refGeometry            input line
     * @param intersectingGeometries intersecting lines
     * @return intersected lines
     */
    private List<LineString> runIntersection(@NotNull Geometry refGeometry, @NotNull List<Geometry> intersectingGeometries) {
        //get intersection points
        CoordinateList intersections = new CoordinateList();
        for (Geometry geometry : intersectingGeometries) {
            intersections.addAll(Arrays.asList(refGeometry.intersection(geometry).getCoordinates()), false);
        }
        //split line
        return splitLine(refGeometry, intersections);
    }

    /**
     * computes line intersection
     *
     * @param refGeometry   input line
     * @param intersections intersection points
     * @return line intersected at intersection points
     */
    private List<LineString> splitLine(@NotNull Geometry refGeometry, @NotNull CoordinateList intersections) {
        List<LineString> geometries = new ArrayList<>();
        GeometryFactory factory = new GeometryFactory();
        //iterate coordinate array
        Coordinate[] refCoords = refGeometry.getCoordinates();
        CoordinateList coordList = new CoordinateList();
        for (int i = 0; i < refCoords.length - 1; i++) {
            //add start point to line
            coordList.add(refCoords[i], false);
            //check if points intersect current segment
            Collection<Coordinate> pointsOnLine = pointsOnLine(refCoords[i], refCoords[i + 1], intersections);
            for (Coordinate coord : pointsOnLine) {
                //add intersection point
                coordList.add(coord, false);
                //add new line; clear list and add intersection point
                if (coordList.size() > 1) {
                    geometries.add(factory.createLineString(coordList.toCoordinateArray()));
                    coordList.clear();
                    coordList.add(coord, false);
                }
            }
        }
        //add final part of line (if not yet set)
        coordList.add(refCoords[refCoords.length - 1], false);
        if (coordList.size() > 1)
            geometries.add(factory.createLineString(coordList.toCoordinateArray()));

        return geometries;
    }

    /**
     * selection of points between start and end (assumes points on line between start and end)
     *
     * @param start         start point
     * @param end           end point
     * @param intersections possible intersections
     * @return intersections between start and end
     */
    private Collection<Coordinate> pointsOnLine(@NotNull Coordinate start, @NotNull Coordinate end, @NotNull CoordinateList intersections) {
        //iterate intersections and setRDFProperty into map (sorted by distance to start coordinate)
        SortedMap<Double, Coordinate> coordMap = new TreeMap<>();
        for (Coordinate coord : intersections.toCoordinateArray()) {
            if (pointOnLine(start, end, coord)) {
                coordMap.put(start.distance(coord), coord);
            }
        }
        return coordMap.values();
    }

    /**
     * check if point is on line
     *
     * @param start start point
     * @param end   end point
     * @param coord input coordinate to check
     * @return true, if coord is located between start and end
     */
    private boolean pointOnLine(@NotNull Coordinate start, @NotNull Coordinate end, @NotNull Coordinate coord) {
        //check if point is between start and end
        return new Envelope(start, end).contains(coord);
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
