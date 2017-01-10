package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
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

import java.util.Map;

/**
 * bounding box distance
 */
public class GeometryDistance extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(GeometryDistance.class.getSimpleName());

    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_DISTANCE.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public GeometryDistance() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = (Geometry) domainFeature.getRepresentation().getDefaultGeometry();
        Geometry gRange = (Geometry) rangeFeature.getRepresentation().getDefaultGeometry();
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        if (gDomain == null || gRange == null)
            return null;
        //check for overlap
        if (gDomain.intersects(gRange))
            return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(0, getMetadataForMeasurement()));
        else {
            //check distance
            double dDistance = getDistance(gDomain, gRange);
            if (dDistance <= dThreshold)
                return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(dDistance, getMetadataForMeasurement()));
            //return null if distance > threshold
            else
                return null;
        }
    }

    /**
     * get distance between geometries
     * @param gDomain reference geometry
     * @param gRange target geometry
     * @return distance (uom defined by input geometries)
     */
    private double getDistance(Geometry gDomain, Geometry gRange){
        return gDomain.distance(gRange);
    }

    @Override
    public String getProcessTitle() {
        return "Geometry distance calculation";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates feature relation based on geometry distance";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "MeasurementData threshold for creating a relation, in map units"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "geometry distance";
    }

    @Override
    protected String getMeasurementDescription() {
        return "distance between geometries";
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
