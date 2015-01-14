package de.tudresden.gis.fusion.operation.similarity.geometry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
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

public class GeometryDistance extends AbstractMeasurementOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#GeometryDistance";
	private final String RELATION_GEOM_INTERSECT = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#intersect";
	private final String RELATION_GEOM_DISTANCE = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#distance_geometric";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		DecimalLiteral inBuffer = (DecimalLiteral) getInput(IN_THRESHOLD);
		
		//set defaults
		double dBuffer = inBuffer == null ? 0 : inBuffer.getValue();
		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS), dBuffer) :
				calculateRelation(inReference, inTarget, dBuffer));
			
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
		
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations, double dThreshold){
		
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = calculateSimilarity(fReference, fTarget, dThreshold);
    		if(similarity != null)
    			relation.addRelationMeasurement(similarity);
	    }
		return relations;
	    
	}
	
	/**
	 * calculate overlap between feature bounds
	 * @param fReference reference feature
	 * @param fTarget target feature
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target, double dThreshold) {
		//get geometries
		Geometry gReference = (Geometry) reference.getDefaultSpatialProperty().getValue();
		Geometry gTarget = (Geometry) target.getDefaultSpatialProperty().getValue();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get overlap
		boolean intersect = getIntersect(gReference, gTarget);
		//check for overlap		
		if(intersect) {
			return new SimilarityMeasurement(
					new BooleanLiteral(intersect), 
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_GEOM_INTERSECT)))
			);
		}
		else {
			//get distance
			double distance = getDistance(gReference, gTarget);
			//check for overlap
			if(distance <= dThreshold)
				return new SimilarityMeasurement(
						new DecimalLiteral(distance), 
						this.getMeasurementDescription(new RelationType(new IRI(RELATION_GEOM_DISTANCE)))
				);
			else
				return null;
		}
	}
	
	/**
	 * check geometry intersection
	 * @param gReference input reference
	 * @param gTarget input target
	 * @return true, if geometries intersect
	 */
	private boolean getIntersect(Geometry gReference, Geometry gTarget){
		return gReference.intersects(gTarget);
	}
	
	/**
	 * get distance between geometries
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return distance (uom defined by input geometries)
	 */
	private double getDistance(Geometry gReference, Geometry gTarget){
		return gReference.distance(gTarget);
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
		return "Calculates the distance between input feature geometries";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_THRESHOLD), "Distance threshold for relations",
					new IDataRestriction[]{
						ERestrictions.BINDING_DECIMAL.getRestriction()
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
					"Geometric intersection between geometries", 
					new RelationType(new IRI(RELATION_GEOM_INTERSECT)),
					new MeasurementRange<Boolean>(
							new BooleanLiteral[]{new BooleanLiteral(true), new BooleanLiteral(false)}, 
							false
					))
		);
		measurements.add(new MeasurementDescription(
					this.getProcessIRI(),
					"Geometric distance between geometries", 
					new RelationType(new IRI(RELATION_GEOM_DISTANCE)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Double.MAX_VALUE)}, 
							true
					))
		);
		return measurements;
	}
	
}
