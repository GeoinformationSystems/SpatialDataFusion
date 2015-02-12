package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector3d;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class AngleDifference extends AbstractMeasurementOperation {

	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_HARDCRITERION = "IN_HARDCRITERION";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#AngleDifference";
	private final String RELATION_ANGLE_DIFF = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#difference_angle";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		DecimalLiteral inThreshold = (DecimalLiteral) getInput(IN_THRESHOLD);
		BooleanLiteral inHardCriterion = (BooleanLiteral) getInput(IN_HARDCRITERION);
		
		//set defaults
		double dThreshold = inThreshold == null ? ((DecimalLiteral) this.getInputDescription(new IRI(IN_THRESHOLD)).getDefault()).getValue() : inThreshold.getValue();
		boolean bHardCriterion = inHardCriterion == null ? ((BooleanLiteral) this.getInputDescription(new IRI(IN_HARDCRITERION)).getDefault()).getValue() : inHardCriterion.getValue();
		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS), dThreshold, bHardCriterion) :
				calculateRelation(inReference, inTarget, dThreshold));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, double dThreshold) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	SimilarityMeasurement similarity = calculateSimilarity(fRef, fTar, dThreshold);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
		
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations, double dThreshold, boolean bHardCriterion){
		//init tmp relations
		IFeatureRelationCollection tmpRelations = new GTFeatureRelationCollection();
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = calculateSimilarity(fReference, fTarget, dThreshold);
    		if(similarity == null && bHardCriterion)
    			continue;
			if(similarity != null)
    			relation.addRelationMeasurement(similarity);
			tmpRelations.addRelation(relation);
	    }
		return tmpRelations;
	    
	}
	
	/**
	 * calculate angle between features
	 * @param fcReference reference feature
	 * @param fcTarget target feature
	 * @param threshold threshold for accepting angle difference
	 * @return angle similarity
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target, double dThreshold) {
		//get linestring geometry
		LineString lReference = getLinestring(reference);
		LineString lTarget = getLinestring(target);
		if(lReference == null || lTarget == null || lReference.isEmpty() || lTarget.isEmpty())
			return null;
		//get angle
		double angle;
		try {
			angle = getAngle(lReference, lTarget);
		} catch (IOException e) {
			return null;
		}
		//add similarity measurement, if angle <= threshold 
		if(angle <= dThreshold){
			return new SimilarityMeasurement( 
					new DecimalLiteral(angle),
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_ANGLE_DIFF)))
			);
		}
		else return null;
	}
	
	/**
	 * calculate angle for single geometries
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return angle (between 0 and PI)
	 * @throws IOException
	 */
	private double getAngle(LineString lReference, LineString lTarget) throws IOException {		
		//get Vectors
		Vector3d vReference = getVector(lReference);
		Vector3d vTarget = getVector(lTarget);
		//get angle [0,PI]
		double angle = vReference.angle(vTarget);
		//get angle [0,PI/2]
		if(angle > Math.PI/2)
			angle = Math.PI - angle;
		//return
		return angle;
	}
	
	/**
	 * get linestring geometry from feature
	 * @param geometry input geometry
	 * @return linestring geometry
	 */
	private LineString getLinestring(IFeature feature) {
		//get default geometry from feature
		Geometry geom = (Geometry) feature.getDefaultSpatialProperty().getValue();
		//get linestring
		if(feature.getDefaultSpatialProperty().getGeometryType().equals(EGeometryType.GML3_1D_CURVE))
			return (LineString) geom;
		else if(feature.getDefaultSpatialProperty().getGeometryType().equals(EGeometryType.GML3_1D_MULTICURVE) && ((MultiLineString) geom).getNumGeometries() == 1)
			return (LineString) ((MultiLineString) geom).getGeometryN(0);
		else
			return null;
	}
	
	/**
	 * get vector of a linestring based on start and end point
	 * @param linestring input linestring
	 * @return vector vector from linestring
	 * @throws IOException if linestring is a loop
	 */
	private Vector3d getVector(LineString line) throws IOException {
		Coordinate[] coords = line.getCoordinates();
		Coordinate first = coords[0];
		Coordinate last = coords[coords.length-1];
		//check for loop
		if(first.equals3D(last))
			throw new IOException("loops are not supported @ " + this.getClass());
		//return vector
		if(!Double.isNaN(first.z) && !Double.isNaN(last.z))
			return new Vector3d(last.x - first.x, last.y - first.y, last.z - first.z);
		else
			return new Vector3d(last.x - first.x, last.y - first.y, 0d);
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
		return "Calculates angle difference between linear input geometries";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.GEOMETRY_LINE.getRestriction()
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
						ERestrictions.GEOMETRY_LINE.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_THRESHOLD), "Angle difference threshold for relations",
					new DecimalLiteral(Math.PI/2),
					new IDataRestriction[]{
						ERestrictions.BINDING_DECIMAL.getRestriction()
					})
		);
		inputs.add(new IODescription(
				new IRI(IN_HARDCRITERION), "flag: measurement is hard criterion for relationship",
				new BooleanLiteral(false),
				new IDataRestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				})
		);
		inputs.add(new IODescription(
					new IRI(IN_RELATIONS), "Input relations; if set, similarity measures are added to the relations (reference and target inputs are ignored)",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
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
	
	@Override
	protected Collection<IMeasurementDescription> getSupportedMeasurements() {
		Collection<IMeasurementDescription> measurements = new ArrayList<IMeasurementDescription>();		
		measurements.add(new MeasurementDescription(
					this.getProcessIRI(),
					"Angle difference between linear geometries", 
					new RelationType(new IRI(RELATION_ANGLE_DIFF)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Math.PI/2)}, 
							true
					))
		);
		return measurements;
	}	

}
