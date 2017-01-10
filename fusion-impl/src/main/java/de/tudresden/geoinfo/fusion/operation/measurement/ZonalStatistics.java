package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.*;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Operations;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForMeasurement;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.metadata.MetadataForMeasurement;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.OperationJAI;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.operation.transform.ProjectiveTransform;
import org.jaitools.media.jai.zonalstats.Result;
import org.jaitools.media.jai.zonalstats.ZonalStats;
import org.jaitools.media.jai.zonalstats.ZonalStatsDescriptor;
import org.jaitools.numeric.Statistic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Length of the intersection between input geometries
 */
public class ZonalStatistics extends AbstractOperation {

    private static final IIdentifier PROCESS = new Identifier(ZonalStatistics.class.getSimpleName());

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_BAND = new Identifier("IN_BAND");
    private final static IIdentifier IN_BUFFER = new Identifier("IN_BUFFER");

    private final static IIdentifier OUT_MEASUREMENTS = new Identifier("OUT_MEASUREMENTS");

    private static final IResource MEASUREMENT_OPERATION_MIN = Operations.ZONAL_STATISTICS_MIN.getResource();
    private static final IResource MEASUREMENT_OPERATION_MAX = Operations.ZONAL_STATISTICS_MAX.getResource();
    private static final IResource MEASUREMENT_OPERATION_MEAN = Operations.ZONAL_STATISTICS_MEAN.getResource();
    private static final IResource MEASUREMENT_OPERATION_SDEV = Operations.ZONAL_STATISTICS_SDEV.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.DECIMAL.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.UNKNOWN.getResource();

    Statistic[] statistics = new Statistic[]{
            Statistic.MIN,
            Statistic.MAX,
            Statistic.MEAN,
            Statistic.SDEV
    };

    private IMetadataForMeasurement measurementDescription_mean;
    private IMetadataForMeasurement measurementDescription_min;
    private IMetadataForMeasurement measurementDescription_max;
    private IMetadataForMeasurement measurementDescription_sdev;

    /**
     * constructor
     */
    public ZonalStatistics() {
        super(PROCESS);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector domainConnector = getInputConnector(IN_DOMAIN);
        IInputConnector rangeConnector = getInputConnector(IN_RANGE);
        IInputConnector bandConnector = getInputConnector(IN_BAND);
        IInputConnector bufferConnector = getInputConnector(IN_BUFFER);
        //get inputs
        GTFeatureCollection domainFeatures = (GTFeatureCollection) domainConnector.getData();
        GTGridFeature rangeGrid = (GTGridFeature) rangeConnector.getData();
        int iBand = ((IntegerLiteral) getInputConnector(IN_BAND).getData()).resolve();
        double dBuffer = ((DecimalLiteral) getInputConnector(IN_BUFFER).getData()).resolve();
        //get transformation to image space
        final AffineTransform gridToWorldTransformCorrected = new AffineTransform((AffineTransform) (rangeGrid.resolve().getGridGeometry()).getGridToCRS2D(PixelOrientation.UPPER_LEFT));
        MathTransform worldToGridTransform;
        try {
            worldToGridTransform = ProjectiveTransform.create(gridToWorldTransformCorrected.createInverse());
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
        //calculate statistics
        connectOutput(OUT_MEASUREMENTS, performZonalStatistics(domainFeatures, rangeGrid, worldToGridTransform, iBand, dBuffer));
    }

    private RelationMeasurementCollection performZonalStatistics(GTFeatureCollection domainFeatures, GTGridFeature rangeGrid, MathTransform worldToGridTransform, int iBand, double dBuffer) {
        RelationMeasurementCollection relationMeasurements = new RelationMeasurementCollection(null, null);
        for(GTVectorFeature domainFeature : domainFeatures){
            Set<IRelationMeasurement> measurements = performZonalStatistics(domainFeature, rangeGrid, worldToGridTransform, iBand, dBuffer);
            if(measurements != null && !measurements.isEmpty())
                relationMeasurements.addAll(measurements);
        }
        return relationMeasurements;
    }


    private Set<IRelationMeasurement> performZonalStatistics(GTVectorFeature domainFeature, GTGridFeature rangeGrid, MathTransform worldToGridTransform, int iBand, double dBuffer) {

        SimpleFeature feature = domainFeature.resolve();
		GridCoverage2D grid = rangeGrid.resolve();

		//get intersection geometry
		Geometry geometry = bufferGeometry((Geometry) feature.getDefaultGeometry(), dBuffer);
		if(geometry == null)
		    return null;

		//get bbox - assumes same CRS for features and grid
        BoundingBox bbox = new ReferencedEnvelope(new ReferencedEnvelope(feature.getBounds()), grid.getCoordinateReferenceSystem());

        //crop coverage by bbox
		CoverageProcessor processor = CoverageProcessor.getInstance();
		ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
		param.parameter("Source").setValue(grid);
		param.parameter("Envelope").setValue(new GeneralEnvelope(bbox));
		GridCoverage2D cropped = (GridCoverage2D) processor.doOperation(param);

		//create ROI
		ROI roi = null;
		int numGeometries = geometry.getNumGeometries();
		for (int i=0; i<numGeometries; i++) {
			Geometry geometryN = geometry.getGeometryN(i);
			java.awt.Polygon awtPolygon;
			try {
				awtPolygon = toAWTPolygon((Polygon) geometryN, worldToGridTransform);
			} catch (TransformException e) {
				throw new RuntimeException(e);
			}
			if (roi == null) {
				roi = new ROIShape(awtPolygon);
			}
			else {
				ROI newRoi = new ROIShape(awtPolygon);
				roi.add(newRoi);
			}
		}

		//init operation
		final OperationJAI op = new OperationJAI("ZonalStats");
		ParameterValueGroup params = op.getParameters();
		params.parameter("dataImage").setValue(cropped);
		params.parameter("stats").setValue(statistics);
		params.parameter("bands").setValue(new Integer[] {iBand});
		params.parameter("roi").setValue(roi);

		//execute operation
		GridCoverage2D zsCoverage;
		try {
			zsCoverage = (GridCoverage2D) op.doOperation(params, null);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}

		//init stats
        Set<IRelationMeasurement> measurements = new HashSet<>();
		ZonalStats stats = (ZonalStats) zsCoverage.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
		for (Result r : stats.results()) {
			if(r.getStatistic() == Statistic.MEAN)
				measurements.add(new RelationMeasurement(null, domainFeature, rangeGrid, DecimalLiteral.getMeasurement(r.getValue(), getMetadataForMeasurement_MEAN())));
			else if(r.getStatistic() == Statistic.MIN)
				measurements.add(new RelationMeasurement(null, domainFeature, rangeGrid, DecimalLiteral.getMeasurement(r.getValue(), getMetadataForMeasurement_MIN())));
			else if(r.getStatistic() == Statistic.MAX)
				measurements.add(new RelationMeasurement(null, domainFeature, rangeGrid, DecimalLiteral.getMeasurement(r.getValue(), getMetadataForMeasurement_MAX())));
			else if(r.getStatistic() == Statistic.SDEV)
				measurements.add(new RelationMeasurement(null, domainFeature, rangeGrid, DecimalLiteral.getMeasurement(r.getValue(), getMetadataForMeasurement_SDEV())));
		}
		return measurements;
	}

	private Geometry bufferGeometry(Geometry geometry, double dBuffer) {
		//throw exception, if no polygon can be created
		if (dBuffer <= 0 && !(geometry instanceof Polygon || geometry instanceof MultiPolygon))
			return null;
		return geometry.buffer(dBuffer);
	}

	/**
	 * generate an AWT polygon from JTS polygon
	 * @param roiInput input polygon
	 * @param worldToGridTransform grid transformation
	 * @return AWT polygon
	 * @throws TransformException
	 */
	private java.awt.Polygon toAWTPolygon(Polygon roiInput, MathTransform worldToGridTransform) throws TransformException {
		boolean isIdentity = worldToGridTransform.isIdentity();
		java.awt.Polygon retValue = new java.awt.Polygon();
		double coords[] = new double[2];
		LineString exteriorRing = roiInput.getExteriorRing();
		CoordinateSequence exteriorRingCS = exteriorRing.getCoordinateSequence();
		int numCoords = exteriorRingCS.size();
		for (int i = 0; i < numCoords; i++) {
			coords[0] = exteriorRingCS.getX(i);
			coords[1] = exteriorRingCS.getY(i);
		if (!isIdentity)
			worldToGridTransform.transform(coords, 0, coords, 0, 1);
			retValue.addPoint((int) Math.round(coords[0] + 0.5d), (int) Math.round(coords[1] + 0.5d));
		}
		return retValue;
	}

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_DOMAIN, new InputConnector(
                IN_DOMAIN,
                new MetadataForConnector(IN_DOMAIN.toString(), "Input domain features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_RANGE, new InputConnector(
                IN_RANGE,
                new MetadataForConnector(IN_RANGE.toString(), "Input range feature grid"),
                new IDataConstraint[]{
                        new BindingConstraint(GTGridFeature.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_BAND, new InputConnector(
                IN_BAND,
                new MetadataForConnector(IN_BAND.toString(), "Coverage band used for statistics calculation"),
                new IDataConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new IntegerLiteral(0)));
        inputConnectors.put(IN_BUFFER, new InputConnector(
                IN_BUFFER,
                new MetadataForConnector(IN_BUFFER.toString(), "Geometry buffer to be applied prior to zonal statistics computation"),
                new IDataConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(0)));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_MEASUREMENTS, new OutputConnector(
                OUT_MEASUREMENTS,
                new MetadataForConnector(OUT_MEASUREMENTS.toString(), "Output relation measurements"),
                new IDataConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null));
        return outputConnectors;
    }

    @Override
    public String getProcessTitle() {
        return "Zonal statistics";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates zonal statistics for domain features in range raster";
    }

    public IMetadataForMeasurement getMetadataForMeasurement_MEAN() {
        if (measurementDescription_mean == null)
            measurementDescription_mean = initMeasurementDescription("Mean", "Zonal mean value", MEASUREMENT_OPERATION_MEAN);
        return measurementDescription_mean;
    }

    public IMetadataForMeasurement getMetadataForMeasurement_MIN() {
        if (measurementDescription_min == null)
            measurementDescription_min = initMeasurementDescription("Min", "Zonal min value", MEASUREMENT_OPERATION_MIN);
        return measurementDescription_min;
    }

    public IMetadataForMeasurement getMetadataForMeasurement_MAX() {
        if (measurementDescription_max == null)
            measurementDescription_max = initMeasurementDescription("Max", "Zonal max value", MEASUREMENT_OPERATION_MAX);
        return measurementDescription_max;
    }

    public IMetadataForMeasurement getMetadataForMeasurement_SDEV() {
        if (measurementDescription_sdev == null)
            measurementDescription_sdev = initMeasurementDescription("Standard deviation", "Zonal Standard deviation", MEASUREMENT_OPERATION_SDEV);
        return measurementDescription_sdev;
    }

    /**
     * initialize measurement description
     */
    public IMetadataForMeasurement initMeasurementDescription(String title, String description, IResource measurementOperation){
        return new MetadataForMeasurement(title, description, measurementOperation, MEASUREMENT_TYPE, DecimalLiteral.getMaxRange(), MEASUREMENT_UNIT);
    }
}