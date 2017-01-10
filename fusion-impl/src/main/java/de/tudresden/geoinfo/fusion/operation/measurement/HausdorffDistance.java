package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Operations;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.InputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.io.IOException;
import java.util.Map;

/**
 * bounding box distance
 */
public class HausdorffDistance extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(HausdorffDistance.class.getSimpleName());

    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");
    private final static IIdentifier IN_BIDIRECTIONAL = new Identifier("IN_BIDIRECTIONAL");
    private final static IIdentifier IN_POINTS_ONLY = new Identifier("IN_POINTS_ONLY");

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_DISTANCE_HAUSDORFF.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public HausdorffDistance() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = (Geometry) domainFeature.getRepresentation().getDefaultGeometry();
        Geometry gRange = (Geometry) rangeFeature.getRepresentation().getDefaultGeometry();
        if (gDomain == null || gRange == null)
            return null;
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();

        //get additional inputs
        boolean bBidirectional = ((BooleanLiteral) getInputConnector(IN_BIDIRECTIONAL).getData()).resolve();
        boolean bPointsOnly = ((BooleanLiteral) getInputConnector(IN_POINTS_ONLY).getData()).resolve();

        //check for overlap
        if (gDomain.intersects(gRange))
            return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(0, getMetadataForMeasurement()));
        else {
            //check distance
            double dDistance = getDistance(gDomain, gRange, bPointsOnly, bBidirectional);
            if (dDistance <= dThreshold)
                return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(dDistance, getMetadataForMeasurement()));
            //return null if distance > threshold
            else
                return null;
        }
    }

    /**
     * calculates hausdorff distance
     * @param gDomain reference geometry
     * @param gRange target geometry
     * @param bBidirectional bidirectional flag
     * @param bPointsOnly points only flag
     * @return hausdorff distance
     * @throws IOException
     */
    private double getDistance(Geometry gDomain, Geometry gRange, boolean bPointsOnly, boolean bBidirectional) {
        if(bPointsOnly && bBidirectional)
            return(Math.min(getDistance(gDomain.getCoordinates(), gRange.getCoordinates()), getDistance(gRange.getCoordinates(), gDomain.getCoordinates())));
        else if(bBidirectional)
            return(Math.min(getDistance(gDomain.getCoordinates(), gRange), getDistance(gRange.getCoordinates(), gDomain)));
        else if(bPointsOnly)
            return(getDistance(gDomain.getCoordinates(), gRange.getCoordinates()));
        else
            return(getDistance(gDomain.getCoordinates(), gRange));
    }

    /**
     * calculates hausdorff distance (points only)
     * @param coords1 reference points
     * @param coords2 target points
     * @return hausdorff distance
     */
    private double getDistance(Coordinate[] coords1, Coordinate[] coords2) {
        double distMin = Double.MAX_VALUE;
        double maxDistMin = Double.MIN_VALUE;
        for(Coordinate coord1 : coords1){
            for(Coordinate coord2 : coords2){
                double dist_tmp = coord1.distance(coord2);
                if(dist_tmp < distMin) distMin = dist_tmp;
            }
            if(distMin > maxDistMin)
                maxDistMin = distMin;
            distMin = 9999;
        }
        return maxDistMin;
    }

    /**
     * calculates hausdorff distance (distance point - geometry)
     * @param coords1 reference points
     * @param target target geometry
     * @return hausdorff distance
     */
    private double getDistance(Coordinate[] coords1, Geometry target) {
        //set default values
        double maxDistMin = Double.MIN_VALUE;
        //convert coordinates to points
        GeometryFactory gf = new GeometryFactory();
        Point[] points = new Point[coords1.length];
        for(int i=0; i<coords1.length; i++){
            points[i] = gf.createPoint(coords1[i]);
        }
        //loop through arrays and find Hausdorff Distance (maximal minimal-distance)
        for(Point point : points){
            double distMin = point.distance(target);
            if(distMin > maxDistMin)
                maxDistMin = distMin;
        }
        return maxDistMin;
    }

    @Override
    public String getProcessTitle() {
        return "Hausdorff distance calculation";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates feature relation based on Hausdorff distance";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_BIDIRECTIONAL, new InputConnector(
                IN_BIDIRECTIONAL,
                new MetadataForConnector(IN_BIDIRECTIONAL.toString(), "If true, the minimum Hausdorff distance is determined bidirectionally"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false)));
        inputConnectors.put(IN_POINTS_ONLY, new InputConnector(
                IN_POINTS_ONLY,
                new MetadataForConnector(IN_POINTS_ONLY.toString(), "If true, the distance is calaculated for points only"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false)));
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "MeasurementData threshold for creating a relation, in map units"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryConstraint()},
                null,
                new DecimalLiteral(0)));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "Hausdorff distance";
    }

    @Override
    protected String getMeasurementDescription() {
        return "Hausdorff distance between geometries";
    }

    @Override
    protected IResource getMeasurementDataType() {
        return MEASUREMENT_TYPE;
    }

    @Override
    protected IResource getMeasurementOperation() {
        return MEASUREMENT_OPERATION;
    }

    @Override
    protected IMeasurementRange getMeasurementRange() {
        return DecimalLiteral.getPositiveRange();
    }

    @Override
    protected IResource getMeasurementUnit() {
        return MEASUREMENT_UNIT;
    }

}
