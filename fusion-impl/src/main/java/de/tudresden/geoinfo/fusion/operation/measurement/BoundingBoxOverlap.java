package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;

import java.util.List;

/**
 * Bounding box overlap
 */
public class BoundingBoxOverlap extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = BoundingBoxOverlap.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on bounding box overlap";

    private static final IMeasurementRange<Boolean> MEASUREMENT_RANGE = BooleanLiteral.getMaxRange();
    private static final String MEASUREMENT_TITLE = "bounding box overlap";
    private static final String MEASUREMENT_DESCRIPTION = "overlap between bounding boxes";
    private static final IResource MEASUREMENT_UNIT = Units.UNDEFINED.getResource();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Distance threshold for creating a relation, in map units";

    private double dThreshold;

    /**
     * constructor
     */
    public BoundingBoxOverlap() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        this.dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD_TITLE).getData()).resolve();
        super.execute();
    }

    @Override
    public RelationMeasurementCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures) {
        //create indexed collection
        GTIndexedFeatureCollection indexedDomainFeatures;
        if (domainFeatures instanceof GTIndexedFeatureCollection)
            indexedDomainFeatures = (GTIndexedFeatureCollection) domainFeatures;
        else
            indexedDomainFeatures = new GTIndexedFeatureCollection(domainFeatures);
        //create relations
        RelationMeasurementCollection measurements = new RelationMeasurementCollection(null, null, null);
        for (GTVectorFeature range : rangeFeatures) {
            List<GTVectorFeature> intersections = indexedDomainFeatures.boundsIntersect(range.resolve(), dThreshold);
            for (GTVectorFeature domain : intersections) {
                measurements.add(new RelationMeasurement<>(null, domain, range, true, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT));
            }
        }
        return measurements;
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Envelope eDomain = ((Geometry) domainFeature.getRepresentation().getDefaultGeometry()).getEnvelopeInternal();
        Envelope eRange = ((Geometry) rangeFeature.getRepresentation().getDefaultGeometry()).getEnvelopeInternal();
        if (eDomain.isNull() || eRange.isNull())
            return null;
        //get overlap
        boolean bIntersect = getIntersect(eDomain, eRange, dThreshold);
        //check for overlap
        if (bIntersect)
            return new RelationMeasurement<>(null, domainFeature, rangeFeature, bIntersect, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT);
        else
            return null;
    }

    /**
     * check geometry intersection
     *
     * @param eDomain input reference
     * @param eRange  input target
     * @return true, if geometries intersect
     */
    private boolean getIntersect(Envelope eDomain, Envelope eRange, double threshold) {
        //expand domain envelope by threshold
        if (threshold > 0)
            eDomain.expandBy(threshold);
        return eDomain.intersects(eRange);
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new InputData(new DecimalLiteral(0)).getOutputConnector());
    }

}
