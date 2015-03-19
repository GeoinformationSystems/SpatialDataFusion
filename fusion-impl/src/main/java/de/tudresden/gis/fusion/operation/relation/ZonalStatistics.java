package de.tudresden.gis.fusion.operation.relation;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
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

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureReference;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.RelationMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTGridCoverage2D;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EMeasurementType;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.metadata.data.IRelationMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementRange;
import de.tudresden.gis.fusion.metadata.data.RelationMeasurementDescription;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

/**
 * zonal statistics operation
 * adapted from https://github.com/geotools/geotools/blob/master/modules/library/coverage/src/test/java/org/geotools/coverage/processing/ZonalStasTest.java
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class ZonalStatistics extends ARelationMeasurementOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_BAND = "IN_BAND";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_LOC.resource(),
			EProcessType.OP_REL_PROP_TOPO.resource()
	};
	
	private final IIRI MEASUREMENT_MIN_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#min");
	private final String MEASUREMENT_MIN_DESC = "Minimum of zonal values";
	private final IIdentifiableResource[] MEASUREMENT_MIN_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_OVERLAP.resource(),
	};
	private final IIRI MEASUREMENT_MAX_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#max");
	private final String MEASUREMENT_MAX_DESC = "Maximum of zonal values";
	private final IIdentifiableResource[] MEASUREMENT_MAX_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_OVERLAP.resource(),
	};
	private final IIRI MEASUREMENT_MEAN_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#mean");
	private final String MEASUREMENT_MEAN_DESC = "Mean of zonal values";
	private final IIdentifiableResource[] MEASUREMENT_MEAN_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_OVERLAP.resource(),
	};
	private final IIRI MEASUREMENT_STD_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#std");
	private final String MEASUREMENT_STD_DESC = "Standard deviation of zonal value";
	private final IIdentifiableResource[] MEASUREMENT_STD_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_OVERLAP.resource(),
	};
	
	Statistic[] statistics = new Statistic[]{
			Statistic.MIN,
			Statistic.MAX,
			Statistic.MEAN,
			Statistic.SDEV
	};
	
	int iBand;

	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		ICoverage inTarget = (ICoverage) getInput(IN_TARGET);
		iBand = ((IntegerLiteral) getInput(IN_BAND)).getValue();
	
		IFeatureRelationCollection relations = null;
		if(inTarget instanceof GTGridCoverage2D && inReference instanceof GTFeatureCollection){
			try {
				relations = calculateRelationsWithJAI((GTFeatureCollection) inReference, (GTGridCoverage2D) inTarget);
			} catch (TransformException e) {
				throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
			}
		}
		else
			// TODO support GDAL coverage with GDAL bindings for Java or Python
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	private IFeatureRelationCollection calculateRelationsWithJAI(GTFeatureCollection reference, GTGridCoverage2D target) throws TransformException {
		
		FeatureReference targetRef = new FeatureReference(target.getIdentifier());
		
		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
		
		final AffineTransform gridToWorldTransformCorrected = new AffineTransform((AffineTransform) ((GridGeometry2D) target.getCoverage().getGridGeometry()).getGridToCRS2D(PixelOrientation.UPPER_LEFT));
		MathTransform worldToGridTransform;
		try {
			worldToGridTransform = ProjectiveTransform.create(gridToWorldTransformCorrected.createInverse());
		} catch (NoninvertibleTransformException e) {
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}

	    for(IFeature fRef : reference.getFeatures()) {
	    	List<RelationMeasurement> measurements = getMeasurements((GTFeature) fRef, target, iBand, worldToGridTransform);
    		if(measurements != null && measurements.size() > 0){
    			for(RelationMeasurement measurement : measurements){
    				relations.addRelation(new FeatureRelation(fRef, targetRef, measurement, null));
    			}
    		}
	    }
	    return relations;
	}
	
	

	private List<RelationMeasurement> getMeasurements(GTFeature feature, GTGridCoverage2D target, int band, MathTransform worldToGridTransform) throws TransformException {
		
		SimpleFeature sf = feature.getFeature();
		GridCoverage2D coverage = target.getCoverage();
				
		Geometry geometry = (Geometry) sf.getDefaultGeometry();
		if (!(geometry instanceof Polygon || geometry instanceof MultiPolygon))
			return null;
		
		BoundingBox bbox = sf.getBounds();
		
		//crop coverage by bbox
		CoverageProcessor processor = CoverageProcessor.getInstance();
		ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
		param.parameter("Source").setValue(coverage);
		param.parameter("Envelope").setValue(new GeneralEnvelope(bbox));
		GridCoverage2D cropped = (GridCoverage2D) processor.doOperation(param);

		//create ROI
		ROI roi = null;
		int numGeometries = geometry.getNumGeometries();
		for (int i=0; i<numGeometries; i++) {
			Geometry geometryN = geometry.getGeometryN(i);
			java.awt.Polygon awtPolygon = toAWTPolygon((Polygon) geometryN, worldToGridTransform);
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
		params.parameter("bands").setValue(new Integer[] {band});
		params.parameter("roi").setValue(roi);
		
		//execute operation
		GridCoverage2D zsCoverage = (GridCoverage2D) op.doOperation(params, null);
		
		//init stats
		List<RelationMeasurement> measurements = new ArrayList<RelationMeasurement>();
		ZonalStats stats = (ZonalStats) zsCoverage.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
		for (Result r : stats.results()) {
			if(r.getStatistic() == Statistic.MEAN) {
				measurements.add(new RelationMeasurement( 
					new DecimalLiteral(r.getValue()),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_MEAN_ID)
				));
		    }
			else if(r.getStatistic() == Statistic.MIN) {
				measurements.add(new RelationMeasurement( 
					new DecimalLiteral(r.getValue()),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_MIN_ID)
				));
		    }
			else if(r.getStatistic() == Statistic.MAX) {
				measurements.add(new RelationMeasurement( 
					new DecimalLiteral(r.getValue()),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_MAX_ID)
				));
		    }
			else if(r.getStatistic() == Statistic.SDEV) {
				measurements.add(new RelationMeasurement( 
					new DecimalLiteral(r.getValue()),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_STD_ID)
				));
		    }
		}
		
		return measurements;
	}
	
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
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessAbstract() {
		return "Determines zonal statistics for reference polygons on target raster";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_REFERENCE, "Reference features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_POLYGON.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_TARGET, "Target coverage",
				new IIORestriction[]{
					ERestrictions.GEOMETRY_SURFACE.getRestriction(),
					ERestrictions.BINDING_ICOVERAGE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_BAND, "Target coverage band",
				new IntegerLiteral(0),
				new IIORestriction[]{
					ERestrictions.BINDING_INTEGER.getRestriction()
				}
			)			
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_RELATIONS, "Output relations",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected IRelationMeasurementDescription[] getSupportedMeasurements() {		
		return new RelationMeasurementDescription[]{
			new RelationMeasurementDescription(
				MEASUREMENT_MIN_ID, MEASUREMENT_MIN_DESC,
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)},
						true
				),
				DataUtilities.toSet(MEASUREMENT_MIN_CLASSIFICATION)
			),
			new RelationMeasurementDescription(
				MEASUREMENT_MAX_ID, MEASUREMENT_MAX_DESC,
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)},
						true
				),
				DataUtilities.toSet(MEASUREMENT_MAX_CLASSIFICATION)
			),
			new RelationMeasurementDescription(
				MEASUREMENT_MEAN_ID, MEASUREMENT_MEAN_DESC,
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)},
						true
				),
				DataUtilities.toSet(MEASUREMENT_MEAN_CLASSIFICATION)
			),
			new RelationMeasurementDescription(
				MEASUREMENT_STD_ID, MEASUREMENT_STD_DESC,
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)},
						true
				),
				DataUtilities.toSet(MEASUREMENT_STD_CLASSIFICATION)
			)
		};
	}

}
