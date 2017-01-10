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
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.InputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.opengis.feature.Feature;

import java.util.Map;

/**
 * Angle difference between linestring geometries
 */
public class LengthDifference extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(LengthDifference.class.getSimpleName());

    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private static final IResource MEASUREMENT_OPERATION = Operations.GEOMETRY_DIFFERENCE_LENGTH.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.MAP_UNITS.getResource();

    /**
     * constructor
     */
    public LengthDifference() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get geometries
        Geometry gReference = getGeometry(((GTVectorRepresentation) domainFeature.getRepresentation()).resolve());
        Geometry gTarget = getGeometry(((GTVectorRepresentation) rangeFeature.getRepresentation()).resolve());
        if(gReference == null || gTarget == null)
			return null;
        //get threshold
        double dThreshold = ((DecimalLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        //get length difference
        double dDiff = getLengthDiff(gReference, gTarget);
        //check for difference
        if(dDiff <= dThreshold) {
            return new RelationMeasurement(null, domainFeature, rangeFeature, DecimalLiteral.getMeasurement(dDiff, getMetadataForMeasurement()));
        }
        else return null;
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
	 * @return reference length - target length
	 */
	private double getLengthDiff(Geometry gDomain, Geometry gRange) {
		return Math.abs(gDomain.getLength() - gRange.getLength());
	}

    @Override
    public String getProcessTitle() {
        return "Length difference calculation";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates feature relation based on geometry length difference";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "MeasurementData threshold for creating a relation, in map units"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "length difference";
    }

    @Override
    protected String getMeasurementDescription() {
        return "length difference between linear geometries";
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
