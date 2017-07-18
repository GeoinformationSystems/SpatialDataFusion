package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
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
 * Length difference between linear geometries
 */
public class LengthDifference extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = LengthDifference.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on geometry length difference";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Length difference";
    private static final String MEASUREMENT_DESCRIPTION = "Length difference between linear geometries";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.MAP_UNITS.getIdentifier();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Difference threshold for creating a relation, in percentage of longer geometry, default: 0.2";

    private double dThreshold;

    /**
     * constructor
     */
    public LengthDifference() {
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
        Geometry gDomain = Utilities.getGeometry(domainFeature, new BindingConstraint(LineString.class, Polygon.class), true);
        Geometry gRange = Utilities.getGeometry(rangeFeature, new BindingConstraint(LineString.class, Polygon.class), true);
        if (gDomain == null || gRange == null)
            return null;
        //get length difference
        double dDiff = getLengthDiff(gDomain, gRange);
        //set threshold
        double dThreshold = Math.max(gDomain.getLength(), gRange.getLength()) * this.dThreshold;
        //check for difference
        if (dDiff <= dThreshold) {
            return new RelationMeasurement<>(new DecimalLiteral(dDiff), domainFeature, rangeFeature, this.getMeasurementMetadata());
        } else return null;
    }

    /**
     * get length difference
     *
     * @param gDomain reference geometry
     * @param gRange  target geometry
     * @return reference length - target length
     */
    private double getLengthDiff(Geometry gDomain, Geometry gRange) {
        return Math.abs(gDomain.getLength() - gRange.getLength());
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(0.2));
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }

}
