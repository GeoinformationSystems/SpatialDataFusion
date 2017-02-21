package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.opengis.feature.Feature;

/**
 * Sinuosity difference between linestring geometries
 */
public class SinuosityDifference extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = SinuosityDifference.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on sinuosity difference";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Sinuosity difference";
    private static final String MEASUREMENT_DESCRIPTION = "Sinuosity difference between linear geometries";
    private static final IResource MEASUREMENT_UNIT = Units.UNDEFINED.getResource();

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
    public void execute() {
        this.dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD_TITLE).getData()).resolve();
        super.execute();
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = getGeometry(((GTVectorRepresentation) domainFeature.getRepresentation()).resolve());
        Geometry gRange = getGeometry(((GTVectorRepresentation) rangeFeature.getRepresentation()).resolve());
        if (gDomain == null || gRange == null)
            return null;
        //get length difference
        double dDiff = getSinuosityDiff(gDomain, gRange);
        //check for difference
        if (dDiff <= dThreshold) {
            return new RelationMeasurement<>(null, domainFeature, rangeFeature, dDiff, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT);
        } else return null;
    }

    /**
     * get linestring geometry from feature
     *
     * @param feature input feature
     * @return linestring geometry
     */
    private Geometry getGeometry(Feature feature) {
        return Utilities.getGeometryFromFeature(feature, new BindingConstraint(LineString.class), true);
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
                        new MandatoryConstraint()},
                null,
                null);
    }

}
