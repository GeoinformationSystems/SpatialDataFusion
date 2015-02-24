package de.tudresden.gis.fusion.operation.relation;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
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
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTGridCoverage2D;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

/**
 * zonal statistics operation
 * adapted from https://github.com/geotools/geotools/blob/master/modules/library/coverage/src/test/java/org/geotools/coverage/processing/ZonalStasTest.java
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class ZonalStatistics extends AbstractMeasurementOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_BAND = "IN_BAND";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#ZonalStatistics";
	
	//relations for zonal statistics
	private final String ZONAL_STATS_MIN = "http://tu-dresden.de/uw/geo/gis/fusion/relation/statistics#min";
	private final String ZONAL_STATS_MAX = "http://tu-dresden.de/uw/geo/gis/fusion/relation/statistics#max";
	private final String ZONAL_STATS_MEAN = "http://tu-dresden.de/uw/geo/gis/fusion/relation/statistics#mean";
	private final String ZONAL_STATS_STD = "http://tu-dresden.de/uw/geo/gis/fusion/relation/statistics#std";
	
	Statistic[] statistics = new Statistic[]{
			Statistic.MIN,
			Statistic.MAX,
			Statistic.MEAN,
			Statistic.SDEV
	};

	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		ICoverage inTarget = (ICoverage) getInput(IN_TARGET);
		IntegerLiteral inBand = (IntegerLiteral) getInput(IN_BAND);
		
		int iBand = inBand == null ? ((IntegerLiteral) this.getInputDescription(new IRI(IN_BAND)).getDefault()).getValue() : inBand.getValue();
	
		IFeatureRelationCollection relations = null;
		if(inTarget instanceof GTGridCoverage2D && inReference instanceof GTFeatureCollection){
			try {
				relations = calculateRelationsWithJAI((GTFeatureCollection) inReference, (GTGridCoverage2D) inTarget, iBand);
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
	
	private IFeatureRelationCollection calculateRelationsWithJAI(GTFeatureCollection reference, GTGridCoverage2D target, int iBand) throws TransformException {
		
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
	    	List<SimilarityMeasurement> measurements = getMeasurements((GTFeature) fRef, target, iBand, worldToGridTransform);
    		if(measurements != null && measurements.size() > 0){
    			for(SimilarityMeasurement measurement : measurements){
    				relations.addRelation(new FeatureRelation(fRef, targetRef, measurement, null));
    			}
    		}
	    }
	    return relations;
	}
	
	

	private List<SimilarityMeasurement> getMeasurements(GTFeature feature, GTGridCoverage2D target, int band, MathTransform worldToGridTransform) throws TransformException {
		
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
		List<SimilarityMeasurement> measurements = new ArrayList<SimilarityMeasurement>();
		ZonalStats stats = (ZonalStats) zsCoverage.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
		for (Result r : stats.results()) {
			if(r.getStatistic() == Statistic.MEAN) {
				measurements.add(new SimilarityMeasurement( 
							new DecimalLiteral(r.getValue()),
							this.getMeasurementDescription(new RelationType(new IRI(ZONAL_STATS_MEAN)))
		    	));
		    }
			else if(r.getStatistic() == Statistic.MIN) {
				measurements.add(new SimilarityMeasurement( 
							new DecimalLiteral(r.getValue()),
							this.getMeasurementDescription(new RelationType(new IRI(ZONAL_STATS_MIN)))
		    	));
		    }
			else if(r.getStatistic() == Statistic.MAX) {
				measurements.add(new SimilarityMeasurement( 
							new DecimalLiteral(r.getValue()),
							this.getMeasurementDescription(new RelationType(new IRI(ZONAL_STATS_MAX)))
		    	));
		    }
			else if(r.getStatistic() == Statistic.SDEV) {
				measurements.add(new SimilarityMeasurement( 
							new DecimalLiteral(r.getValue()),
							this.getMeasurementDescription(new RelationType(new IRI(ZONAL_STATS_STD)))
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
	protected Collection<IMeasurementDescription> getSupportedMeasurements() {
		Collection<IMeasurementDescription> measurements = new ArrayList<IMeasurementDescription>();		
		measurements.add(new MeasurementDescription(
				this.getProcessIRI(),
				"minimum target coverage value for reference feature", 
				new RelationType(new IRI(ZONAL_STATS_MIN)),
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)}, 
						true
				))
		);
		measurements.add(new MeasurementDescription(
				this.getProcessIRI(),
				"maximum target coverage value for reference feature", 
				new RelationType(new IRI(ZONAL_STATS_MAX)),
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)}, 
						true
				))
		);
		measurements.add(new MeasurementDescription(
				this.getProcessIRI(),
				"mean target coverage value for reference feature", 
				new RelationType(new IRI(ZONAL_STATS_MEAN)),
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)}, 
						true
				))
		);
		measurements.add(new MeasurementDescription(
				this.getProcessIRI(),
				"standard deviation of target coverage values for reference feature", 
				new RelationType(new IRI(ZONAL_STATS_STD)),
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)}, 
						true
				))
		);
		return measurements;
	}

	@Override
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Determines zonal statistics for reference polygons on target raster";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
				new IRI(IN_REFERENCE), "Reference polygon features",
				new IDataRestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_POLYGON.getRestriction()
				})
		);
		inputs.add(new IODescription(
				new IRI(IN_TARGET), "Target coverage",
				new IDataRestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_ICOVERAGE.getRestriction(),
					ERestrictions.GEOMETRY_SURFACE.getRestriction()
				})
		);
		inputs.add(new IODescription(
				new IRI(IN_BAND), "Target coverage band",
				new IntegerLiteral(0),
				new IDataRestriction[]{
					ERestrictions.BINDING_INTEGER.getRestriction()
				})
		);
		return inputs;
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		Collection<IIODescription> outputs = new ArrayList<IIODescription>();
		outputs.add(new IODescription(
					new IRI(OUT_RELATIONS), "Output relations",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}

}
