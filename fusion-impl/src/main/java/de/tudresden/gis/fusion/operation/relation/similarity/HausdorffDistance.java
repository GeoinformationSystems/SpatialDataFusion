package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EMeasurementType;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementRange;
import de.tudresden.gis.fusion.metadata.data.SimilarityMeasurementDescription;
import de.tudresden.gis.fusion.operation.ASimilarityMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class HausdorffDistance extends ASimilarityMeasurementOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_BIDIRECTIONAL = "IN_BIDIRECTIONAL";
	private final String IN_POINTS_ONLY = "IN_POINTS_ONLY";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_LOC.resource()
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#distance");
	private final String MEASUREMENT_DESC = "Hausdorff distance between geometries";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.GEOM_DIST_EUC.resource()
	};
	
	private double dThreshold;
	private boolean bBidirectional;
	private boolean bPointsOnly;
	private boolean bDropRelations;
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		bBidirectional = ((BooleanLiteral) getInput(IN_BIDIRECTIONAL)).getValue();
		bPointsOnly = ((BooleanLiteral) getInput(IN_POINTS_ONLY)).getValue();
		dThreshold = ((DecimalLiteral) getInput(IN_THRESHOLD)).getValue();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).getValue();
		
		//execute		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				relate(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS)) :
				relate(inReference, inTarget));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	@Override
	protected boolean dropRelations() {
		return bDropRelations;
	}
	
	@Override
	protected SimilarityMeasurement relate(IFeature reference, IFeature target) {
		Geometry gReference = (Geometry) reference.getDefaultSpatialProperty().getValue();
		Geometry gTarget = (Geometry) target.getDefaultSpatialProperty().getValue();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get distance
		double dDistance = calculateHausdorffDistance(gReference, gTarget);
		//add similarity measurement, if angle <= threshold 
		if(dDistance <= dThreshold){
			return new SimilarityMeasurement( 
				new DecimalLiteral(dDistance),
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_ID)
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
	private double calculateHausdorffDistance(Geometry gReference, Geometry gTarget) {
		//calculate hausdorff distance for each feature
		if(bPointsOnly && bBidirectional)
			return(Math.min(calculateHausdorffDistance(gReference.getCoordinates(), gTarget.getCoordinates()), calculateHausdorffDistance(gTarget.getCoordinates(), gReference.getCoordinates())));
		else if(bBidirectional)
			return(Math.min(calculateHausdorffDistance(gReference.getCoordinates(), gTarget), calculateHausdorffDistance(gTarget.getCoordinates(), gReference)));
		else if(bPointsOnly)
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
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}
	
	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
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
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_REFERENCE, "Reference features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_TARGET, "Target features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_THRESHOLD, "Distance threshold for relations",
				new IIORestriction[]{
					ERestrictions.BINDING_DECIMAL.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_BIDIRECTIONAL, "Flag: bidirectional distance calculation",
				new BooleanLiteral(false),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			),
			new IODescription(
					IN_POINTS_ONLY, "Flag: use only points for distance calculation",
				new BooleanLiteral(false),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			),
			new IODescription(
				IN_DROP_RELATIONS, "relations that do not satisfy the threshold are dropped",
				new BooleanLiteral(false),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			),
			new IODescription(
				IN_RELATIONS, "Input relations; if set, similarity measures are added to the relations (reference and target inputs are ignored)",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
		)};
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
	protected ISimilarityMeasurementDescription[] getSupportedMeasurements() {		
		return new SimilarityMeasurementDescription[]{
			new SimilarityMeasurementDescription(
				MEASUREMENT_ID, MEASUREMENT_DESC,
				new MeasurementRange<Double>(
						new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Double.MAX_VALUE)},
						true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}
	
}
