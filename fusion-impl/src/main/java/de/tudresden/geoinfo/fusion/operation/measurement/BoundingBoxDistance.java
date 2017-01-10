package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
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
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.InputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;

import java.util.List;
import java.util.Map;

/**
 * bounding box overlap
 */
public class BoundingBoxDistance extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(BoundingBoxDistance.class.getSimpleName());

    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_BBOX_DISTANCE.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public BoundingBoxDistance() {
        super(PROCESS);
    }

    @Override
    public RelationMeasurementCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures){
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        //create indexed collection
        GTIndexedFeatureCollection indexedDomainFeatures;
        if(domainFeatures instanceof GTIndexedFeatureCollection)
            indexedDomainFeatures = (GTIndexedFeatureCollection) domainFeatures;
        else
            indexedDomainFeatures = new GTIndexedFeatureCollection(domainFeatures);
        //create relations
        RelationMeasurementCollection measurements = new RelationMeasurementCollection(null, null);
        for(GTVectorFeature range : rangeFeatures){
            List<GTVectorFeature> intersections = indexedDomainFeatures.boundsIntersect(range.resolve(), dThreshold);
            for(GTVectorFeature domain : intersections){
                measurements.add(new RelationMeasurement(null, domain, range, BooleanLiteral.getMeasurement(true, getMetadataForMeasurement())));
            }
        }
        return measurements;
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Envelope eDomain = ((Geometry) domainFeature.getRepresentation().getDefaultGeometry()).getEnvelopeInternal();
        Envelope eRange = ((Geometry) rangeFeature.getRepresentation().getDefaultGeometry()).getEnvelopeInternal();
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        if (eDomain.isNull() || eRange.isNull())
            return null;
        //get overlap
        boolean bIntersect = getIntersect(eDomain, eRange, dThreshold);
        //check for overlap
        if (bIntersect)
            return new RelationMeasurement(null, domainFeature, rangeFeature, BooleanLiteral.getMeasurement(true, getMetadataForMeasurement()));
        else
            return null;
    }

    /**
     * check geometry intersection
     * @param eDomain input reference
     * @param eRange input target
     * @return true, if geometries intersect
     */
    private boolean getIntersect(Envelope eDomain, Envelope eRange, double threshold){
        //expand domain envelope by threshold
        if(threshold > 0)
            eDomain.expandBy(threshold);
        return eDomain.intersects(eRange);
    }

    @Override
    public String getProcessTitle() {
        return "Bounding box overlap";
    }

    @Override
    public String getProcessAbstract() {
        return "Determines feature relation based on bounding box overlap";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "Distance threshold for allowable distance between bounding boxes, in map units"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(0)));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "bounding box overlap";
    }

    @Override
    protected String getMeasurementDescription() {
        return "overlap between bounding boxes";
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
        return BooleanLiteral.getMaxRange();
    }

    @Override
    protected IResource getMeasurementUnit() {
        return MEASUREMENT_UNIT;
    }

}
