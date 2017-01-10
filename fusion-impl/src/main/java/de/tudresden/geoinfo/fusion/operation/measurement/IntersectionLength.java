package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import de.tud.fusion.GeoToolsUtilityClass;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorRepresentation;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Operations;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.opengis.feature.Feature;

/**
 * Length of the intersection between input geometries
 */
public class IntersectionLength extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(IntersectionLength.class.getSimpleName());

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_INTERSECTION_LENGTH.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public IntersectionLength() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gDomain = getGeometry(((GTVectorRepresentation) domainFeature.getRepresentation()).resolve());
        Geometry gRange = getGeometry(((GTVectorRepresentation) rangeFeature.getRepresentation()).resolve());
        if(gDomain == null || gRange == null || !gDomain.intersects(gRange))
            return null;
        //get intersection length
        double intersectionLength = getIntersectionLength(gDomain, gRange);
        return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(intersectionLength, getMetadataForMeasurement()));
    }

    /**
     * get linestring geometry from feature
     * @param feature input feature
     * @return linestring geometry
     */
    private Geometry getGeometry(Feature feature) {
        return GeoToolsUtilityClass.getGeometryFromFeature(feature, new BindingConstraint(LineString.class, Polygon.class), true);
    }

    /**
     * get length difference
     * @param gDomain reference geometry
     * @param gRange target geometry
     * @return intersection length
     */
    private double getIntersectionLength(Geometry gDomain, Geometry gRange) {
        Geometry intersection = gDomain.intersection(gRange);
        //return ratio intersection/domain length
		return intersection.getLength() / gDomain.getLength();
    }

    @Override
    public String getProcessTitle() {
        return "Intersection length between geometries";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates intersection length between input geometries";
    }

    @Override
    protected String getMeasurementTitle() {
        return "Intersection length";
    }

    @Override
    protected String getMeasurementDescription() {
        return "Length of an intersection, in percentage of domain geometry length";
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
    protected IMeasurementRange<Double> getMeasurementRange() {
        return DecimalLiteral.getPositiveRange();
    }

    @Override
    protected IResource getMeasurementUnit() {
        return MEASUREMENT_UNIT;
    }

}
