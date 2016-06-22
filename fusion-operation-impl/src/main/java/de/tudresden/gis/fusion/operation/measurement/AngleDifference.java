package de.tudresden.gis.fusion.operation.measurement;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import javax.vecmath.Vector3d;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.MeasurementRange;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
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

public class AngleDifference extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private double dThreshold;
	private boolean bDropRelations;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;

	private MeasurementDescription measurementDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DIFFERENCE_ANGLE.asString(),
			"angle difference",
			"angle difference between linear geometries",
			new MeasurementRange(new TreeSet<DecimalLiteral>(Arrays.asList(new DecimalLiteral(0d), new DecimalLiteral(Math.PI/2))), true),
			RDFVocabulary.UOM_RADIAN_ANGLE.asResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) getInput(IN_TARGET);
		dThreshold = ((DecimalLiteral) getInput(IN_THRESHOLD)).resolve();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).resolve();
		
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
		LineString gReference = getLinestring(((GTFeature) reference).getDefaultGeometry());
		LineString gTarget = getLinestring(((GTFeature) target).getDefaultGeometry());
		//get angle
		double dAngle = getAngle((LineString) gReference, (LineString) gTarget);
		//check for overlap		
		if(dAngle <= dThreshold) {
			return getMeasurements(new RelationMeasurement(
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					new DecimalLiteral(dAngle), 
					measurementDescription));
		}
		else return null;
	}
	
	/**
	 * check input geometry
	 * @param gReference input geometry
	 * @throws ProcessException if geometry is not a valid input for this process
	 */
	private LineString getLinestring(Geometry geometry) throws ProcessException {
		//check for LineString and MultiLineString
		if(!(geometry instanceof LineString) && !(geometry instanceof MultiLineString))
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Input is not a valid LineString or MultiLineString");
		//take first Geometry if MultiLineString with one geometry
		if(geometry instanceof MultiLineString)
			if(((MultiLineString) geometry).getNumGeometries() == 1)
				geometry = ((MultiLineString) geometry).getGeometryN(0);
			else
				throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Input MultiLineString must not have more than 1 element");
		//check for closed LineString
		if(((LineString) geometry).isClosed())
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "LineString must not be closed");
		return (LineString) geometry;
	}

	/**
	 * calculate angle between two linestrings
	 * @param gReference reference linestring
	 * @param gTarget target linestring
	 * @return angle (between 0 and PI/2)
	 */
	private double getAngle(LineString lReference, LineString lTarget) {		
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
	 * get vector of a linestring based on start and end point
	 * @param linestring input linestring
	 * @return vector vector from linestring
	 */
	private Vector3d getVector(LineString line) {
		Coordinate[] coords = line.getCoordinates();
		Coordinate first = coords[0];
		Coordinate last = coords[coords.length-1];
		//return vector
		if(!Double.isNaN(first.z) && !Double.isNaN(last.z))
			return new Vector3d(last.x - first.x, last.y - first.y, last.z - first.z);
		else
			return new Vector3d(last.x - first.x, last.y - first.y, 0d);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Angle difference calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on geometry angle difference";
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
			inputDescriptions.add(new InputDescription(IN_THRESHOLD, IN_THRESHOLD, "Threshold for angle difference",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{DecimalLiteral.class})
					},
					new DecimalLiteral(Math.PI/2)));
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
					OUT_RELATIONS, OUT_RELATIONS, "Output relations with angle measurement",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
		}
		return outputDescriptions;
	}

}
