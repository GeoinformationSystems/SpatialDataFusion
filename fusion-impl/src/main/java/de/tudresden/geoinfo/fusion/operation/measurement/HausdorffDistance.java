package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Hausdorff distance
 */
public class HausdorffDistance extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = HausdorffDistance.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on Hausdorff distance";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Hausdorff distance";
    private static final String MEASUREMENT_DESCRIPTION = "Hausdorff between geometries";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.MAP_UNITS.getIdentifier();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Distance threshold for creating a relation, in map units";
    private static final String IN_BIDIRECTIONAL_TITLE = "IN_BIDIRECTIONAL";
    private static final String IN_BIDIRECTIONAL_DESCRIPTION = "Flag: calculate bidirectional Hausdorff distance";
    private static final String IN_POINTS_ONLY_TITLE = "IN_POINTS_ONLY";
    private static final String IN_POINTS_ONLY_DESCRIPTION = "Flag: calculate Hausdorff distance for points only";


    private double dThreshold;
    private boolean bBidirectional;
    private boolean bPointsOnly;

    /**
     * constructor
     */
    public HausdorffDistance() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {
        this.dThreshold = ((DecimalLiteral) this.getMandatoryInputData(IN_THRESHOLD_TITLE)).resolve();
        this.bBidirectional = ((BooleanLiteral) this.getMandatoryInputData(IN_BIDIRECTIONAL_TITLE)).resolve();
        this.bPointsOnly = ((BooleanLiteral) this.getMandatoryInputData(IN_POINTS_ONLY_TITLE)).resolve();
        super.executeOperation();
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature) {

        //get geometries
        Geometry gDomain = Utilities.getGeometry(domainFeature);
        Geometry gRange = Utilities.getGeometry(rangeFeature);
        if (gDomain == null || gRange == null)
            return null;

        //check for overlap
        if (gDomain.intersects(gRange))
            return new RelationMeasurement<>(new DecimalLiteral(0d), domainFeature, rangeFeature, this.getMeasurementMetadata());
        else {
            //check distance
            double dDistance = getHDDistance(gDomain, gRange);
            if (dDistance <= dThreshold)
                return new RelationMeasurement<>(new DecimalLiteral(dDistance), domainFeature, rangeFeature, this.getMeasurementMetadata());
                //return null if distance > threshold
            else
                return null;
        }
    }

    /**
     * calculates hausdorff distance
     *
     * @param gDomain reference geometry
     * @param gRange  target geometry
     * @return hausdorff distance
     * @throws IOException
     */
    private double getHDDistance(Geometry gDomain, Geometry gRange) {
        if (bPointsOnly && bBidirectional)
            return (Math.min(getHDDistance(gDomain.getCoordinates(), gRange.getCoordinates()), getHDDistance(gRange.getCoordinates(), gDomain.getCoordinates())));
        else if (bBidirectional)
            return (Math.min(getHDDistance(gDomain.getCoordinates(), gRange), getHDDistance(gRange.getCoordinates(), gDomain)));
        else if (bPointsOnly)
            return (getHDDistance(gDomain.getCoordinates(), gRange.getCoordinates()));
        else
            return (getHDDistance(gDomain.getCoordinates(), gRange));
    }

    /**
     * calculates hausdorff distance (points only)
     *
     * @param coords1 reference points
     * @param coords2 target points
     * @return hausdorff distance
     */
    private double getHDDistance(Coordinate[] coords1, Coordinate[] coords2) {
        double distMin = Double.MAX_VALUE;
        double maxDistMin = Double.MIN_VALUE;
        for (Coordinate coord1 : coords1) {
            for (Coordinate coord2 : coords2) {
                double dist_tmp = coord1.distance(coord2);
                if (dist_tmp < distMin) distMin = dist_tmp;
            }
            if (distMin > maxDistMin)
                maxDistMin = distMin;
            distMin = 9999;
        }
        return maxDistMin;
    }

    /**
     * calculates hausdorff distance (distance point - geometry)
     *
     * @param coords1 reference points
     * @param target  target geometry
     * @return hausdorff distance
     */
    private double getHDDistance(Coordinate[] coords1, Geometry target) {
        //set default values
        double maxDistMin = Double.MIN_VALUE;
        //convert coordinates to points
        GeometryFactory gf = new GeometryFactory();
        Point[] points = new Point[coords1.length];
        for (int i = 0; i < coords1.length; i++) {
            points[i] = gf.createPoint(coords1[i]);
        }
        //loop through arrays and find Hausdorff Distance (maximal minimal-distance)
        for (Point point : points) {
            double distMin = point.distance(target);
            if (distMin > maxDistMin)
                maxDistMin = distMin;
        }
        return maxDistMin;
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_BIDIRECTIONAL_TITLE, IN_BIDIRECTIONAL_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
        addInputConnector(IN_POINTS_ONLY_TITLE, IN_POINTS_ONLY_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                new DecimalLiteral(0));
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }

}
