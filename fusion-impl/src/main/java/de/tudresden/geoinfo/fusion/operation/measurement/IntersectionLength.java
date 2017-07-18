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
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;

/**
 * Length of the intersection between input geometries
 */
public class IntersectionLength extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = IntersectionLength.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on intersection length";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Intersection length";
    private static final String MEASUREMENT_DESCRIPTION = "Length of an intersection, in percentage of domain geometry length";
    private static final IIdentifier MEASUREMENT_UNIT = MetadataVocabulary.MAP_UNITS.getIdentifier();

    /**
     * constructor
     */
    public IntersectionLength() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = Utilities.getGeometry(domainFeature, new BindingConstraint(LineString.class, Polygon.class), true);
        Geometry gRange = Utilities.getGeometry(rangeFeature, new BindingConstraint(LineString.class, Polygon.class), true);
        if (gDomain == null || gRange == null || !gDomain.intersects(gRange))
            return null;
        //get intersection length
        double intersectionLength = getIntersectionLength(gDomain, gRange);
        return new RelationMeasurement<>(new DecimalLiteral(intersectionLength), domainFeature, rangeFeature, this.getMeasurementMetadata());
    }

    /**
     * get length difference
     *
     * @param gDomain reference geometry
     * @param gRange  target geometry
     * @return intersection length
     */
    private double getIntersectionLength(Geometry gDomain, Geometry gRange) {
        Geometry intersection = gDomain.intersection(gRange);
        //return ratio intersection/domain length
        return intersection.getLength() / gDomain.getLength();
    }

    @NotNull
    @Override
    public IMetadata initMeasurementMetadata() {
        return new Metadata(MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE, this);
    }
}
