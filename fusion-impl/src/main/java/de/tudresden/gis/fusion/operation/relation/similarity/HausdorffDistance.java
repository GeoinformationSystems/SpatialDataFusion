package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

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

public class HausdorffDistance extends AbstractMeasurementOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_BIDIRECTIONAL = "IN_BIDIRECTIONAL";
	private final String IN_POINTS_ONLY = "IN_POINTS_ONLY";
	private final String IN_RELATIONS = "IN_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#HausdorffDistance";
	private final String RELATION_HAUS_DISTANCE = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#distance_hausdorff";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		DecimalLiteral inThreshold = (DecimalLiteral) getInput(IN_THRESHOLD);
		BooleanLiteral inBidirectional = (BooleanLiteral) getInput(IN_BIDIRECTIONAL);
		BooleanLiteral inPointsOnly = (BooleanLiteral) getInput(IN_POINTS_ONLY);
		
		//set defaults
		double dThreshold = inThreshold.getValue();
		boolean bidirectional = inBidirectional == null ? ((BooleanLiteral) this.getInputDescription(new IRI(IN_BIDIRECTIONAL)).getDefault()).getValue() : inBidirectional.getValue();
		boolean pointsOnly = inPointsOnly == null ? ((BooleanLiteral) this.getInputDescription(new IRI(IN_POINTS_ONLY)).getDefault()).getValue() : inPointsOnly.getValue();
		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS), dThreshold, bidirectional, pointsOnly) :
				calculateRelation(inReference, inTarget, dThreshold, bidirectional, pointsOnly));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, double dThreshold, boolean bidirectional, boolean pointsOnly) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	SimilarityMeasurement similarity = calculateSimilarity(fRef, fTar, dThreshold, bidirectional, pointsOnly);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
	
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations, double dThreshold, boolean bidirectional, boolean pointsOnly){
		
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = calculateSimilarity(fReference, fTarget, dThreshold, bidirectional, pointsOnly);
    		if(similarity != null)
    			relation.addRelationMeasurement(similarity);
	    }
		return relations;
	    
	}
	
	private SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target, double dThreshold, boolean bidirectional, boolean pointsOnly) {
		Geometry gReference = (Geometry) reference.getDefaultSpatialProperty().getValue();
		Geometry gTarget = (Geometry) target.getDefaultSpatialProperty().getValue();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get distance
		double distance = calculateHausdorffDistance(gReference, gTarget, bidirectional, pointsOnly);
		//add similarity measurement, if angle <= threshold 
		if(distance <= dThreshold){
			return new SimilarityMeasurement(
					new DecimalLiteral(distance), 
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_HAUS_DISTANCE)))
			);
		}
		else return null;
	}
	
	/**
	 * calculates hausdorff distance
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @param bidirectional bidirectional flag
	 * @param pointsOnly points only flag
	 * @return hausdorff distance
	 * @throws IOException
	 */
	private double calculateHausdorffDistance(Geometry gReference, Geometry gTarget, boolean bidirectional, boolean pointsOnly) {
		//calculate hausdorff distance for each feature
		if(pointsOnly && bidirectional)
			return(Math.min(calculateHausdorffDistance(gReference.getCoordinates(), gTarget.getCoordinates()), calculateHausdorffDistance(gTarget.getCoordinates(), gReference.getCoordinates())));
		else if(bidirectional)
			return(Math.min(calculateHausdorffDistance(gReference.getCoordinates(), gTarget), calculateHausdorffDistance(gTarget.getCoordinates(), gReference)));
		else if(pointsOnly)
			return(calculateHausdorffDistance(gReference.getCoordinates(), gTarget.getCoordinates()));		
		else
			return(calculateHausdorffDistance(gReference.getCoordinates(), gTarget));
	}
	
	/**
	 * calculates hausdorff distance (points only)
	 * @param coords1 reference points
	 * @param coords2 target points
	 * @return hausdorff distance
	 */
	private double calculateHausdorffDistance(Coordinate[] coords1, Coordinate[] coords2) {
		//set default values
		double distMin = Double.MAX_VALUE; 
		double maxDistMin = Double.MIN_VALUE;
		//loop through arrays and find Hausdorff Distance (maximal minimal-distance)
		for(Coordinate coord1 : coords1){                	
        	for(Coordinate coord2 : coords2){
        		double dist_tmp = coord1.distance(coord2);
        		if(dist_tmp < distMin) distMin = dist_tmp;                		
        	}
        	if(distMin > maxDistMin)
        		maxDistMin = distMin;
        	distMin = 9999;
    	}
		return maxDistMin;
	}
	
	/**
	 * calculates hausdorff distance (distance point - geometry)
	 * @param coords1 reference points
	 * @param target target geometry
	 * @return hausdorff distance
	 */
	private double calculateHausdorffDistance(Coordinate[] coords1, Geometry target) {
		//set default values
		double maxDistMin = Double.MIN_VALUE;
		//convert coordinates to points
		GeometryFactory gf = new GeometryFactory();
		Point[] points = new Point[coords1.length];
		for(int i=0; i<coords1.length; i++){
			points[i] = gf.createPoint(coords1[i]);
		}
		//loop through arrays and find Hausdorff Distance (maximal minimal-distance)
		for(Point point : points){             	
        	double distMin = point.distance(target);               		
        	if(distMin > maxDistMin)
        		maxDistMin = distMin;
    	}
		return maxDistMin;
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
		return "Calculates the Hausdorff distance between input feature geometries";
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
				new IRI(IN_BIDIRECTIONAL), "Flag: bidirectional distance calculation",
				new BooleanLiteral(false),
				new IDataRestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				})
		);
		inputs.add(new IODescription(
					new IRI(IN_POINTS_ONLY), "Flag: use only points for distance calculation",
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
					"Hausdorff distance between input geometries", 
					new RelationType(new IRI(RELATION_HAUS_DISTANCE)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Double.MAX_VALUE)}, 
							true
					))
		);
		return measurements;
	}
	
}
