package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Geometry distance
 */
public class GeometryDistance extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = GeometryDistance.class.getName();
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
    public GeometryDistance(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        this.dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD_TITLE).getData()).resolve();
        super.executeOperation();
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
            return new RelationMeasurement<>(null, domainFeature, rangeFeature, 0d, this.getMeasurementMetadata(), this);
        else {
            //check distance
            double dDistance = getDistance(gDomain, gRange);
            if (dDistance <= dThreshold)
                return new RelationMeasurement<>(null, domainFeature, rangeFeature, dDistance, this.getMeasurementMetadata(), this);
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
        addInputConnector(null, IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE);
    }

    @NotNull
    @Override
    public String getTitle() {
        return PROCESS_TITLE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PROCESS_DESCRIPTION;
    }

}
