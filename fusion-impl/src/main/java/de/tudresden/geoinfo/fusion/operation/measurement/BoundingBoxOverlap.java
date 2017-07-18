package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Envelope;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataVocabulary;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bounding box overlap
 */
public class BoundingBoxOverlap extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = BoundingBoxOverlap.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on bounding box overlap";

    private static final IMeasurementRange<Boolean> MEASUREMENT_RANGE = BooleanLiteral.getMaxRange();
    private static final String MEASUREMENT_TITLE = "bounding box overlap";
    private static final String MEASUREMENT_DESCRIPTION = "overlap between bounding boxes";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.UNDEFINED.getIdentifier();

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
    public void executeOperation() {
        this.dThreshold = ((DecimalLiteral) this.getMandatoryInputData(IN_THRESHOLD_TITLE)).resolve();
        super.executeOperation();
    }

    @NotNull
    @Override
    public RelationMeasurementCollection performRelationMeasurement(@NotNull GTFeatureCollection domainFeatures, @NotNull GTFeatureCollection rangeFeatures) {
        //create indexed collection
        GTIndexedFeatureCollection indexedDomainFeatures;
        if (domainFeatures instanceof GTIndexedFeatureCollection)
            indexedDomainFeatures = (GTIndexedFeatureCollection) domainFeatures;
        else
            indexedDomainFeatures = new GTIndexedFeatureCollection(domainFeatures);
        //create relations
        RelationMeasurementCollection measurements = new RelationMeasurementCollection(new ResourceIdentifier(), null, null);
        for (GTVectorFeature range : rangeFeatures) {
            List<GTVectorFeature> intersections = indexedDomainFeatures.boundsIntersect(range.resolve(), dThreshold);
            for (GTVectorFeature domain : intersections) {
                measurements.add(new RelationMeasurement<>(new BooleanLiteral(true), domain, range, this.getMeasurementMetadata()));
            }
        }
        return measurements;
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature) {
        //get geometries
        Envelope eDomain = Utilities.getEnvelope(domainFeature);
        Envelope eRange = Utilities.getEnvelope(rangeFeature);
        if (eDomain == null || eRange == null)
            return null;
        //get overlap
        boolean bIntersect = getIntersect(eDomain, eRange, dThreshold);
        //check for overlap
        if (bIntersect)
            return new RelationMeasurement<>(new BooleanLiteral(true), domainFeature, rangeFeature, this.initMeasurementMetadata());
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
                new DecimalLiteral(0));
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }

}
