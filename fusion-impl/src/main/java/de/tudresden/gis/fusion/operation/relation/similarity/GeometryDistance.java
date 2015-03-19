package de.tudresden.gis.fusion.operation.relation.similarity;

import com.vividsolutions.jts.geom.Geometry;

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

public class GeometryDistance extends ASimilarityMeasurementOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_LOC.resource()
	};
	
	private final IIRI MEASUREMENT_INTERSECT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#intersect");
	private final String MEASUREMENT_INTERSECT_DESC = "Geometry intersection";
	private final IIdentifiableResource[] MEASUREMENT_INTERSECT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_INTERSECT.resource(),
	};
	private final IIRI MEASUREMENT_DIST_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#distance");
	private final String MEASUREMENT_DIST_DESC = "Distance between geometries";
	private final IIdentifiableResource[] MEASUREMENT_DIST_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.GEOM_DIST_EUC.resource()
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
		//get geometries
		Geometry gReference = (Geometry) reference.getDefaultSpatialProperty().getValue();
		Geometry gTarget = (Geometry) target.getDefaultSpatialProperty().getValue();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get overlap
		boolean bIntersect = getIntersect(gReference, gTarget);
		//check for overlap		
		if(bIntersect) {
			return new SimilarityMeasurement( 
				new BooleanLiteral(bIntersect),
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_INTERSECT_ID)
			);
		}
		else {
			//get distance
			double dDistance = getDistance(gReference, gTarget);
			//check for overlap
			if(dDistance <= dThreshold)
				return new SimilarityMeasurement( 
					new DecimalLiteral(dDistance),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_DIST_ID)
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
	protected String getProcessAbstract() {
		return "Calculates the distance between input feature geometries";
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
				new DecimalLiteral(0),
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
					MEASUREMENT_INTERSECT_ID, MEASUREMENT_INTERSECT_DESC,
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(100)},
							true
					),
					DataUtilities.toSet(MEASUREMENT_INTERSECT_CLASSIFICATION)
			),
			new SimilarityMeasurementDescription(
					MEASUREMENT_DIST_ID, MEASUREMENT_DIST_DESC,
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Double.MAX_VALUE)},
							true
					),
					DataUtilities.toSet(MEASUREMENT_DIST_CLASSIFICATION)
			)
		};
	}
	
}
