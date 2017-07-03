package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.Feature;

/**
 * Length of the intersection between input geometries
 */
public class IntersectionLength extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = IntersectionLength.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines feature relation based on intersection length";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Intersection length";
    private static final String MEASUREMENT_DESCRIPTION = "Length of an intersection, in percentage of domain geometry length";
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public IntersectionLength(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = getGeometry(((GTVectorRepresentation) domainFeature.getRepresentation()).resolve());
        Geometry gRange = getGeometry(((GTVectorRepresentation) rangeFeature.getRepresentation()).resolve());
        if (gDomain == null || gRange == null || !gDomain.intersects(gRange))
            return null;
        //get intersection length
        double intersectionLength = getIntersectionLength(gDomain, gRange);
        return new RelationMeasurement<>(null, domainFeature, rangeFeature, intersectionLength, this.getMeasurementMetadata(), this);
    }

    /**
     * get linestring geometry from feature
     *
     * @param feature input feature
     * @return linestring geometry
     */
    private Geometry getGeometry(Feature feature) {
        return Utilities.getGeometryFromFeature(feature, new BindingConstraint(LineString.class, Polygon.class), true);
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
