package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;

/**
 * Geometry distance
 */
public class GeometryDistance extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = GeometryDistance.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on bounding box overlap";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Geometry distance";
    private static final String MEASUREMENT_DESCRIPTION = "Distance between geometries";
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Distance threshold for creating a relation, in map units";

    private double dThreshold;

    /**
     * constructor
     */
    public GeometryDistance() {
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
        Geometry gDomain = (Geometry) domainFeature.getRepresentation().getDefaultGeometry();
        Geometry gRange = (Geometry) rangeFeature.getRepresentation().getDefaultGeometry();
        if (gDomain == null || gRange == null)
            return null;
        //check for overlap
        if (gDomain.intersects(gRange))
            return new RelationMeasurement<>(null, domainFeature, rangeFeature, 0d, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT);
        else {
            //check distance
            double dDistance = getDistance(gDomain, gRange);
            if (dDistance <= dThreshold)
                return new RelationMeasurement<>(null, domainFeature, rangeFeature, dDistance, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT);
                //return null if distance > threshold
            else
                return null;
        }
    }

    /**
     * get distance between geometries
     *
     * @param gDomain reference geometry
     * @param gRange  target geometry
     * @return distance (uom defined by input geometries)
     */
    private double getDistance(Geometry gDomain, Geometry gRange) {
        return gDomain.distance(gRange);
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

}
