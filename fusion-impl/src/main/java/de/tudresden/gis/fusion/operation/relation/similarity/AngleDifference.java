package de.tudresden.gis.fusion.operation.relation.similarity;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.vecmath.Vector3d;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
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

public class AngleDifference extends ASimilarityMeasurementOperation {

	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_ORIENTATION.resource()
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle());
	private final String MEASUREMENT_DESC = "Angle difference between linear geometries";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.GEOM_SHAPE_DIFF.resource(),
			EMeasurementType.DIFFERENCE.resource()
	};
	
	private double dThreshold;
	private boolean bDropRelations;
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		dThreshold = ((DecimalLiteral) getInput(IN_THRESHOLD)).getValue();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).getValue();
		
		//execute
		IFeatureRelationCollection relations = inputContainsKey(IN_RELATIONS) ?
				relate(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS)) :
				relate(inReference, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
	}
	
	@Override
	protected boolean dropRelations() {
		return bDropRelations;
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
	protected SimilarityMeasurement relate(IFeature reference, IFeature target) {
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
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_ID)
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
		return "Calculates angle difference between linear input geometries";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_REFERENCE, "Reference features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_LINE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_TARGET, "Target features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_LINE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_THRESHOLD, "Angle difference threshold for relations",
				new DecimalLiteral(Math.PI/2),
				new IIORestriction[]{
					ERestrictions.BINDING_DECIMAL.getRestriction()
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
						new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Math.PI/2)}, 
						true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}

}
