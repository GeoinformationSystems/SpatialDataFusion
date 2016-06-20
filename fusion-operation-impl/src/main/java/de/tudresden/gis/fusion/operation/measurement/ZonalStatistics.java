package de.tudresden.gis.fusion.operation.measurement;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.OperationJAI;
import org.geotools.geometry.GeneralEnvelope;
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

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTGridCoverage;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class ZonalStatistics extends ARelationMeasurementOperation {

	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_BAND = "IN_BAND";
	private final String IN_BUFFER = "IN_BUFFER";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private int iBand;
	private double dBuffer;
	private boolean bDropRelations;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;

	private MeasurementDescription measurementDescription_mean = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_RASTER_ZONAL_STATS_MEAN.asString(),
			"zonal statistics - mean",
			"mean value of zonal statistics",
			DecimalLiteral.maxRange(),
			RDFVocabulary.UOM_UNKNOWN.asResource());
	
	private MeasurementDescription measurementDescription_min = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_RASTER_ZONAL_STATS_MIN.asString(),
			"zonal statistics - min",
			"min value of zonal statistics",
			DecimalLiteral.maxRange(),
			RDFVocabulary.UOM_UNKNOWN.asResource());
	
	private MeasurementDescription measurementDescription_max = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_RASTER_ZONAL_STATS_MAX.asString(),
			"zonal statistics - max",
			"max value of zonal statistics",
			DecimalLiteral.maxRange(),
			RDFVocabulary.UOM_UNKNOWN.asResource());
	
	private MeasurementDescription measurementDescription_std = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_RASTER_ZONAL_STATS_STD.asString(),
			"zonal statistics - std",
			"standard deviation of zonal statistics",
			DecimalLiteral.positiveRange(),
			RDFVocabulary.UOM_UNKNOWN.asResource());
	
	Statistic[] statistics = new Statistic[]{
			Statistic.MIN,
			Statistic.MAX,
			Statistic.MEAN,
			Statistic.SDEV
	};
	
	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
		GTGridCoverage inTarget = (GTGridCoverage) getInput(IN_TARGET);
		
		iBand = ((IntegerLiteral) getInput(IN_BAND)).resolve();
		dBuffer = ((DecimalLiteral) getInput(IN_BUFFER)).resolve();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).resolve();
		
		//execute
		IDataCollection<IFeatureRelation> relations = 
				inputContainsKey(IN_RELATIONS) ?
						relations(inSource, inTarget, (FeatureRelationCollection) getInput(IN_RELATIONS)) :
						relations(inSource, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	protected IDataCollection<IFeatureRelation> relations(GTFeatureCollection inSource, GTGridCoverage inTarget, IDataCollection<IFeatureRelation> existingRelations){
		//create relation collection
		FeatureRelationCollection relations = new FeatureRelationCollection();
		//add relation measurement if relation already exists & measurement != null
		for(IFeatureRelation existingRelation : existingRelations){
			IFeature ref = existingRelation.getSource();
			IFeature tar = existingRelation.getTarget();
			IFeatureRelation relation = relation(ref, tar, existingRelation, bDropRelations);
			if(relation != null)
				relations.add(relation);
		}
		return relations;
	}
	
	private IDataCollection<IFeatureRelation> relations(GTFeatureCollection inSource, GTGridCoverage inTarget) {
		//create relation collection
		FeatureRelationCollection relations = new FeatureRelationCollection();
		//add relations
		for(GTFeature source : inSource){
			IFeatureRelation relation = relation(source, inTarget, null, true);
			if(relation != null)
				relations.add(relation);
		}
		return relations;
	}

	@Override
	protected IRelationMeasurement[] getMeasurements(IFeature fSource, IFeature fTarget){
		
		SimpleFeature source = (SimpleFeature) ((GTFeature) fSource).resolve();
		GridCoverage2D target = ((GTGridCoverage) fTarget).resolve();
		
		//get transformation to image space
		final AffineTransform gridToWorldTransformCorrected = new AffineTransform((AffineTransform) ((GridGeometry2D) target.getGridGeometry()).getGridToCRS2D(PixelOrientation.UPPER_LEFT));
		MathTransform worldToGridTransform;
		try {
			worldToGridTransform = ProjectiveTransform.create(gridToWorldTransformCorrected.createInverse());
		} catch (NoninvertibleTransformException e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, e.getLocalizedMessage());
		}
		
		//get source geometry
		Geometry geometry = bufferGeometry((Geometry) source.getDefaultGeometry());
		
		//get bounding box of feature
		BoundingBox bbox = source.getBounds();
		
		//crop coverage by bbox
		CoverageProcessor processor = CoverageProcessor.getInstance();
		ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
		param.parameter("Source").setValue(target);
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
				throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, e.getLocalizedMessage());
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
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, e.getLocalizedMessage());
		}
		
		//init stats
		List<IRelationMeasurement> measurements = new ArrayList<IRelationMeasurement>();
		ZonalStats stats = (ZonalStats) zsCoverage.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
		for (Result r : stats.results()) {
			if(r.getStatistic() == Statistic.MEAN) {
				measurements.add(new RelationMeasurement(
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						RDFVocabulary.PROPERTY_THEM.asResource(),
						new DecimalLiteral(r.getValue()), 
						measurementDescription_mean));
		    }
			if(r.getStatistic() == Statistic.MIN) {
				measurements.add(new RelationMeasurement(
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						RDFVocabulary.PROPERTY_THEM.asResource(),
						new DecimalLiteral(r.getValue()), 
						measurementDescription_min));
		    }
			if(r.getStatistic() == Statistic.MAX) {
				measurements.add(new RelationMeasurement(
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						RDFVocabulary.PROPERTY_THEM.asResource(),
						new DecimalLiteral(r.getValue()), 
						measurementDescription_max));
		    }
			if(r.getStatistic() == Statistic.SDEV) {
				measurements.add(new RelationMeasurement(
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						RDFVocabulary.PROPERTY_THEM.asResource(),
						new DecimalLiteral(r.getValue()), 
						measurementDescription_std));
		    }
		}
		return getMeasurements(measurements);
	}
	
	private Geometry bufferGeometry(Geometry geometry) {
		//throw exception, if no polygon can be created
		if (dBuffer <= 0 && !(geometry instanceof Polygon || geometry instanceof MultiPolygon))
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "input must be polygon type if IN_BUFFER is not specified");

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
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Zonal statistics";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates zonal statistics between source featrures and target coverage";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(
					IN_SOURCE, IN_SOURCE,"Reference features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_SOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_TARGET, IN_TARGET, "Target features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_TARGET),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTGridCoverage.class})
					}));
			inputDescriptions.add(new InputDescription(IN_BAND, IN_BAND, "Coverage band used for statistics calculation",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{IntegerLiteral.class})
					},
					new IntegerLiteral(0)));
			inputDescriptions.add(new InputDescription(IN_BUFFER, IN_BUFFER, "Geometry buffer to be applied before zonal statisitcs computation",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{DecimalLiteral.class})
					}));
			inputDescriptions.add(new InputDescription(IN_RELATIONS, IN_RELATIONS, "If set, relation measures are added to existing relations (reference and target inputs are ignored)",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_DROP_RELATIONS, IN_DROP_RELATIONS, "If true, relations that do not satisfy the threshold are dropped",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					},
					new BooleanLiteral(false)));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_RELATIONS, OUT_RELATIONS, "Output relations with zonal statistics relation",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
		}
		return outputDescriptions;
	}
	
}
