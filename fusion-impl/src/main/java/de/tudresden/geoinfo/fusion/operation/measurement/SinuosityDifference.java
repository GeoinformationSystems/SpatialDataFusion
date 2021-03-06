package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
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
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;

/**
 * Sinuosity difference between linestring geometries
 */
public class SinuosityDifference extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = SinuosityDifference.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on sinuosity difference";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Sinuosity difference";
    private static final String MEASUREMENT_DESCRIPTION = "Sinuosity difference between linear geometries";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.UNDEFINED.getIdentifier();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Sinuosity difference threshold for creating a relation";

    private double dThreshold;

    /**
     * constructor
     */
    public SinuosityDifference() {
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
        Geometry gDomain = Utilities.getGeometry(domainFeature, new BindingConstraint(LineString.class), true);
        Geometry gRange = Utilities.getGeometry(rangeFeature, new BindingConstraint(LineString.class), true);
        if (gDomain == null || gRange == null)
            return null;
        //get length difference
        double dDiff = getSinuosityDiff(gDomain, gRange);
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
    private double getSinuosityDiff(Geometry gDomain, Geometry gRange) {
        return Math.abs(gDomain.getLength() - gRange.getLength());
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }

}
