package de.tudresden.gis.fusion.operation.measurement;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class HausdorffDistance extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	private final String IN_BIDIRECTIONAL = "IN_BIDIRECTIONAL";
	private final String IN_POINTS_ONLY = "IN_POINTS_ONLY";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private double dThreshold;
	private boolean bDropRelations;
	private boolean bBidirectional;
	private boolean bPointsOnly;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DISTANCE_HAUSDORFF.getString(),
			"Hausdorff distance",
			"Hausdorff distance between feature geometries",
			DecimalLiteral.positiveRange(),
			RDFVocabulary.UOM_UNKNOWN.getResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) getInput(IN_TARGET);
		dThreshold = ((DecimalLiteral) getInput(IN_THRESHOLD)).resolve();
		
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).resolve();
		bBidirectional = ((BooleanLiteral) getInput(IN_BIDIRECTIONAL)).resolve();
		bPointsOnly = ((BooleanLiteral) getInput(IN_POINTS_ONLY)).resolve();
		
		//execute
		IDataCollection<IFeatureRelation> relations = 
				inputContainsKey(IN_RELATIONS) ?
						relations(inSource, inTarget, (FeatureRelationCollection) getInput(IN_RELATIONS), bDropRelations) :
						relations(inSource, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	@Override
	protected IRelationMeasurement[] getMeasurements(IFeature reference, IFeature target){
		//get geometries
		Geometry gReference = ((GTFeature) reference).getDefaultGeometry();
		Geometry gTarget = ((GTFeature) target).getDefaultGeometry();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get overlap
		double dDistance = getDistance(gReference, gTarget);
		//check for overlap		
		if(dDistance <= dThreshold) {
			return getMeasurements(new RelationMeasurement(
					null, 
					RDFVocabulary.PROPERTY_GEOM.getResource(),
					RDFVocabulary.PROPERTY_GEOM.getResource(),
					new DecimalLiteral(dDistance), 
					distanceDescription));
		}		
		else 
			return null;
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
	private double getDistance(Geometry gReference, Geometry gTarget) {
		if(bPointsOnly && bBidirectional)
			return(Math.min(getDistance(gReference.getCoordinates(), gTarget.getCoordinates()), getDistance(gTarget.getCoordinates(), gReference.getCoordinates())));
		else if(bBidirectional)
			return(Math.min(getDistance(gReference.getCoordinates(), gTarget), getDistance(gTarget.getCoordinates(), gReference)));
		else if(bPointsOnly)
			return(getDistance(gReference.getCoordinates(), gTarget.getCoordinates()));		
		else
			return(getDistance(gReference.getCoordinates(), gTarget));
	}
	
	/**
	 * calculates hausdorff distance (points only)
	 * @param coords1 reference points
	 * @param coords2 target points
	 * @return hausdorff distance
	 */
	private double getDistance(Coordinate[] coords1, Coordinate[] coords2) {
		double distMin = Double.MAX_VALUE; 
		double maxDistMin = Double.MIN_VALUE;
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
	private double getDistance(Coordinate[] coords1, Geometry target) {
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
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Hausdorff distance calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on Hausdorff (maximal minimum) distance between geometries";
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
					IN_SOURCE, IN_SOURCE, "Reference features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_SOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_TARGET, IN_TARGET, "Target features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_TARGET),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_THRESHOLD, IN_THRESHOLD, "Threshold for Hausdorff distance",
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
			inputDescriptions.add(new InputDescription(IN_BIDIRECTIONAL, IN_BIDIRECTIONAL, "If true, the minimum Hausdorff distance is determined bidirectionally",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					},
					new BooleanLiteral(false)));
			inputDescriptions.add(new InputDescription(IN_POINTS_ONLY, IN_POINTS_ONLY, "If true, the distance is calaculated for points only",
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
					OUT_RELATIONS, OUT_RELATIONS, "Output relations with Hausdorff distance relation",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
		}
		return outputDescriptions;
	}
}
