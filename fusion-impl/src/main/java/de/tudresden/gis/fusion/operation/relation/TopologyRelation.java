package de.tudresden.gis.fusion.operation.relation;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;

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
import de.tudresden.gis.fusion.data.simple.StringLiteral;
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

public class TopologyRelation extends ASimilarityMeasurementOperation {

	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_LOC.resource(),
			EProcessType.OP_REL_PROP_TOPO.resource()
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle());
	private final String MEASUREMENT_DESC = "DE-9IM topology relation of geometry";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_INTERSECT.resource(),
			EMeasurementType.TOPO_OVERLAP.resource(),
	};
	
	private boolean bDropRelations;
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).getValue();
	
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
		//get intersection matrix
		IntersectionMatrix matrix = gReference.relate(gTarget);
		//add DE-9IM topology relation if geometries are not disjoint
		if(!matrix.isDisjoint()){
			return new SimilarityMeasurement( 
				new StringLiteral(matrix.toString()),
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_ID)
			);
		}
		else return null;
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
		return "Determines topology relation between input geometries, stored as DE-9IM";
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
				IN_DROP_RELATIONS, "relations that are distinct are dropped",
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
				new MeasurementRange<String>(
					new StringLiteral[]{new StringLiteral("FFFFFFFFF"), new StringLiteral("TTTTTTTTT")}, 
					true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}
	
}
