package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.math.Vector3D;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;

/**
 * Angle difference between linestring geometries
 */
public class AngleDifference extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = AngleDifference.class.getName();
    private static final String PROCESS_DESCRIPTION = "Angle difference between linear geometries";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getRange(0d, Math.PI / 2);
    private static final String MEASUREMENT_TITLE = "Angle difference";
    private static final String MEASUREMENT_DESCRIPTION = "Angle difference between linear geometries";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.DEGREE_ANGLE.getIdentifier();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Measurement threshold for creating a relation, in degree";

    private double dThreshold;

    /**
     * constructor
     */
    public AngleDifference() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {
        this.dThreshold = ((DecimalLiteral) this.getMandatoryInputData(IN_THRESHOLD_TITLE)).resolve();
        super.executeOperation();
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = Utilities.getLineString(domainFeature);
        Geometry gRange = Utilities.getLineString(rangeFeature);
        if (gDomain == null || gRange == null)
            return null;
        //get angle
        double dAngle = getAngle(gDomain, gRange);
        //check for difference
        if (dAngle <= dThreshold) {
            return new RelationMeasurement<>(new DecimalLiteral(dAngle), domainFeature, rangeFeature, this.getMeasurementMetadata());
        } else return null;
    }

    /**
     * calculate angle between two linestrings
     *
     * @param gDomain reference linestring
     * @param gRange  target linestring
     * @return angle (between 0 and PI/2)
     */
    private double getAngle(Geometry gDomain, Geometry gRange) {
        //get Vectors
        Vector3D vDomain = getVector(gDomain);
        Vector3D vRange = getVector(gRange);
        //get angle [0,PI]
        double angle = getAngle(vDomain, vRange);
        //get angle [0,PI/2]
        if (angle > Math.PI / 2)
            angle = Math.PI - angle;
        //return
        return angle;
    }

    /**
     * calculate angle between vectors
     *
     * @param vDomain reference vector
     * @param vRange  target vector
     * @return angle between reference and target vector
     */
    private double getAngle(Vector3D vDomain, Vector3D vRange) {
        double dot = vDomain.dot(vRange) / (vDomain.length() * vRange.length());
        if (dot < -1.0) dot = -1.0;
        if (dot > 1.0) dot = 1.0;
        return Math.acos(dot);
    }

    /**
     * get vector of a linestring based on start and end point
     *
     * @param geometry input linestring
     * @return vector vector from linestring
     */
    private Vector3D getVector(Geometry geometry) {
        Coordinate[] coords = geometry.getCoordinates();
        Coordinate first = coords[0];
        Coordinate last = coords[coords.length - 1];
        //return vector
        if (!Double.isNaN(first.z) && !Double.isNaN(last.z))
            return new Vector3D(last.x - first.x, last.y - first.y, last.z - first.z);
        else
            return new Vector3D(last.x - first.x, last.y - first.y, 0d);
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(Math.PI / 8));
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }

}
