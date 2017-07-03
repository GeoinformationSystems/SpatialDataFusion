package de.tudresden.geoinfo.fusion.operation.measurement;

import com.vividsolutions.jts.geom.*;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurementCollection;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Length of the intersection between input geometries
 */
public class ZonalStatistics extends AbstractOperation {

    private static final String PROCESS_TITLE = ZonalStatistics.class.getName();
    private static final String PROCESS_DESCRIPTION = "Determines relations based on zonal statistics for domain polygons on range raster";

    private final static String IN_DOMAIN_TITLE = "IN_DOMAIN";
    private final static String IN_DOMAIN_DESCRIPTION = "Domain polygon features";
    private final static String IN_RANGE_TITLE = "IN_RANGE";
    private final static String IN_RANGE_DESCRIPTION = "Range raster feature";
    private final static String IN_BAND_TITLE = "IN_BAND";
    private final static String IN_BAND_DESCRIPTION = "Raster band used for statistics calculation";
    private final static String IN_BUFFER_TITLE = "IN_BUFFER";
    private final static String IN_BUFFER_DESCRIPTION = "Geometry buffer to be applied prior to zonal statistics computation";

    private final static String OUT_MEASUREMENTS_TITLE = "OUT_MEASUREMENTS";
    private final static String OUT_MEASUREMENTS_DESCRIPTION = "Relation measurements";

    private static final IMeasurementRange<Double> MEASUREMENT_RANGE = DecimalLiteral.getMaxRange();
    private static final IResource MEASUREMENT_UNIT = Units.UNKNOWN.getResource();
    private static final String MEASUREMENT_MIN_TITLE = "Min";
    private static final String MEASUREMENT_MIN_DESCRIPTION = "Zonal min value";
    private static final String MEASUREMENT_MAX_TITLE = "Min";
    private static final String MEASUREMENT_MAX_DESCRIPTION = "Zonal max value";
    private static final String MEASUREMENT_MEAN_TITLE = "Mean";
    private static final String MEASUREMENT_MEAN_DESCRIPTION = "Zonal mean value";
    private static final String MEASUREMENT_SDEV_TITLE = "SDev";
    private static final String MEASUREMENT_SDEV_DESCRIPTION = "Zonal standard deviation";

    private IMetadata measurementMetadata_MIN, measurementMetadata_MAX, measurementMetadata_MEAN, measurementMetadata_SDEV;

    private Statistic[] statistics = new Statistic[]{
            Statistic.MIN,
            Statistic.MAX,
            Statistic.MEAN,
            Statistic.SDEV
    };

    /**
     * constructor
     */
    public ZonalStatistics(@Nullable IIdentifier identifier) {
        super(identifier);
        this.measurementMetadata_MIN = new Metadata(MEASUREMENT_MIN_TITLE, MEASUREMENT_MIN_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE);
        this.measurementMetadata_MAX = new Metadata(MEASUREMENT_MAX_TITLE, MEASUREMENT_MAX_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE);
        this.measurementMetadata_MEAN = new Metadata(MEASUREMENT_MEAN_TITLE, MEASUREMENT_MEAN_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE);
        this.measurementMetadata_SDEV = new Metadata(MEASUREMENT_SDEV_TITLE, MEASUREMENT_SDEV_DESCRIPTION, MEASUREMENT_UNIT, MEASUREMENT_RANGE);
    }

    @Override
    public void executeOperation() {
        //get input connectors
        IInputConnector domainConnector = getInputConnector(IN_DOMAIN_TITLE);
        IInputConnector rangeConnector = getInputConnector(IN_RANGE_TITLE);
        IInputConnector bandConnector = getInputConnector(IN_BAND_TITLE);
        IInputConnector bufferConnector = getInputConnector(IN_BUFFER_TITLE);
        //get inputs
        GTFeatureCollection domainFeatures = (GTFeatureCollection) domainConnector.getData();
        GTGridFeature rangeGrid = (GTGridFeature) rangeConnector.getData();
        int iBand = ((IntegerLiteral) bandConnector.getData()).resolve();
        double dBuffer = ((DecimalLiteral) bufferConnector.getData()).resolve();
        //get transformation to image space
        final AffineTransform gridToWorldTransformCorrected = new AffineTransform((AffineTransform) (rangeGrid.resolve().getGridGeometry()).getGridToCRS2D(PixelOrientation.UPPER_LEFT));
        MathTransform worldToGridTransform;
        try {
            worldToGridTransform = ProjectiveTransform.create(gridToWorldTransformCorrected.createInverse());
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
        //calculate statistics
        connectOutput(OUT_MEASUREMENTS_TITLE, performZonalStatistics(domainFeatures, rangeGrid, worldToGridTransform, iBand, dBuffer));
    }

    private RelationMeasurementCollection performZonalStatistics(GTFeatureCollection domainFeatures, GTGridFeature rangeGrid, MathTransform worldToGridTransform, int iBand, double dBuffer) {
        RelationMeasurementCollection relationMeasurements = new RelationMeasurementCollection(null, null, null);
        for (GTVectorFeature domainFeature : domainFeatures) {
            Set<IRelationMeasurement> measurements = performZonalStatistics(domainFeature, rangeGrid, worldToGridTransform, iBand, dBuffer);
            if (measurements != null && !measurements.isEmpty())
                relationMeasurements.addAll(measurements);
        }
        return relationMeasurements;
    }


    private Set<IRelationMeasurement> performZonalStatistics(GTVectorFeature domainFeature, GTGridFeature rangeGrid, MathTransform worldToGridTransform, int iBand, double dBuffer) {

        SimpleFeature feature = domainFeature.resolve();
        GridCoverage2D grid = rangeGrid.resolve();

        //get intersection geometry
        Geometry geometry = bufferGeometry((Geometry) feature.getDefaultGeometry(), dBuffer);
        if (geometry == null)
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
        for (int i = 0; i < numGeometries; i++) {
            Geometry geometryN = geometry.getGeometryN(i);
            java.awt.Polygon awtPolygon;
            try {
                awtPolygon = toAWTPolygon((Polygon) geometryN, worldToGridTransform);
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
            if (roi == null) {
                roi = new ROIShape(awtPolygon);
            } else {
                ROI newRoi = new ROIShape(awtPolygon);
                roi.add(newRoi);
            }
        }

        //init operation
        final OperationJAI op = new OperationJAI("ZonalStats");
        ParameterValueGroup params = op.getParameters();
        params.parameter("dataImage").setValue(cropped);
        params.parameter("stats").setValue(statistics);
        params.parameter("bands").setValue(new Integer[]{iBand});
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
            if (r.getStatistic() == Statistic.MEAN)
                measurements.add(new RelationMeasurement<>(null, domainFeature, rangeGrid, r.getValue(), measurementMetadata_MEAN, this));
            else if (r.getStatistic() == Statistic.MIN)
                measurements.add(new RelationMeasurement<>(null, domainFeature, rangeGrid, r.getValue(), measurementMetadata_MIN, this));
            else if (r.getStatistic() == Statistic.MAX)
                measurements.add(new RelationMeasurement<>(null, domainFeature, rangeGrid, r.getValue(), measurementMetadata_MAX, this));
            else if (r.getStatistic() == Statistic.SDEV)
                measurements.add(new RelationMeasurement<>(null, domainFeature, rangeGrid, r.getValue(), measurementMetadata_SDEV, this));
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
     *
     * @param roiInput             input polygon
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
    public void initializeInputConnectors() {
        addInputConnector(null, IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTGridFeature.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_BAND_TITLE, IN_BAND_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new IntegerLiteral(0));
        addInputConnector(null, IN_BUFFER_TITLE, IN_BUFFER_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                new DecimalLiteral(0));
    }


    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_MEASUREMENTS_TITLE, OUT_MEASUREMENTS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null);
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