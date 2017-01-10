package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector3D;
import de.tud.fusion.GeoToolsUtilityClass;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
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
import org.opengis.feature.Feature;

import java.util.Map;

/**
 * Angle difference between linestring geometries
 */
public class AngleDifference extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(AngleDifference.class.getSimpleName());

    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_DIFFERENCE_ANGLE.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.DEGREE_ANGLE.getResource();

    /**
     * constructor
     */
    public AngleDifference() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = getGeometry(((GTVectorRepresentation) domainFeature.getRepresentation()).resolve());
        Geometry gRange = getGeometry(((GTVectorRepresentation) rangeFeature.getRepresentation()).resolve());
        if(gDomain == null || gRange == null)
            return null;
        //get threshold
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        //get angle
        double dAngle = getAngle(gDomain, gRange);
        //check for difference
        if(dAngle <= dThreshold) {
            return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(dAngle, getMetadataForMeasurement()));
        }
        else return null;
    }

    /**
     * get linestring geometry from feature
     * @param feature input feature
     * @return linestring geometry
     */
    private Geometry getGeometry(Feature feature) {
        return GeoToolsUtilityClass.getGeometryFromFeature(feature, new BindingConstraint(LineString.class), true);
    }

    /**
     * calculate angle between two linestrings
     * @param gDomain reference linestring
     * @param gRange target linestring
     * @return angle (between 0 and PI/2)
     */
    private double getAngle(Geometry gDomain, Geometry gRange) {
        //get Vectors
        Vector3D vDomain = getVector(gDomain);
        Vector3D vRange = getVector(gRange);
        //get angle [0,PI]
        double angle = getAngle(vDomain, vRange);
        //get angle [0,PI/2]
        if(angle > Math.PI/2)
            angle = Math.PI - angle;
        //return
        return angle;
    }

    /**
     * calculate angle between vectors
     * @param vDomain reference vector
     * @param vRange target vector
     * @return angle between reference and target vector
     */
    private double getAngle(Vector3D vDomain, Vector3D vRange) {
        double dot = vDomain.dot(vRange) / ( vDomain.length()*vRange.length() );
        if(dot < -1.0) dot = -1.0;
        if(dot >  1.0) dot =  1.0;
        return Math.acos(dot);
    }

    /**
     * get vector of a linestring based on start and end point
     * @param geometry input linestring
     * @return vector vector from linestring
     */
    private Vector3D getVector(Geometry geometry) {
        Coordinate[] coords = geometry.getCoordinates();
        Coordinate first = coords[0];
        Coordinate last = coords[coords.length-1];
        //return vector
        if(!Double.isNaN(first.z) && !Double.isNaN(last.z))
            return new Vector3D(last.x - first.x, last.y - first.y, last.z - first.z);
        else
            return new Vector3D(last.x - first.x, last.y - first.y, 0d);
    }

    @Override
    public String getProcessTitle() {
        return "Angle difference calculation";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates feature relation based on geometry angle difference";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "MeasurementData threshold for creating a relation, in degree"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(Math.PI/8)));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "angle difference";
    }

    @Override
    protected String getMeasurementDescription() {
        return "angle difference between linear geometries";
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
    protected IMeasurementRange<Double> getMeasurementRange() {
        return DecimalLiteral.getRange(0d, Math.PI/2);
    }

    @Override
    protected IResource getMeasurementUnit() {
        return MEASUREMENT_UNIT;
    }

}
