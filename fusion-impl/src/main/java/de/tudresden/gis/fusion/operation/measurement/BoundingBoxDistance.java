package de.tudresden.gis.fusion.operation.measurement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.geotools.GTIndexedFeatureCollection;
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

public class BoundingBoxDistance extends ARelationMeasurementOperation {
	
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
	
	private MeasurementDescription intersectDescription = new MeasurementDescription(
			RDFVocabulary.SF_INTERSECTS.getString(),
			"intersection",
			"intersection between feature bounding boxes",
			BooleanLiteral.maxRange(),
			RDFVocabulary.UOM_UNDEFINED.getResource());
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DISTANCE.getString(),
			"distance",
			"minimum distance between feature bounding boxes",
			DecimalLiteral.positiveRange(),
			RDFVocabulary.UOM_UNKNOWN.getResource());

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
						relationsWithIndex(new GTIndexedFeatureCollection(inSource), inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	private FeatureRelationCollection relationsWithIndex(GTIndexedFeatureCollection reference, GTFeatureCollection target) {
		//create relation collection
		FeatureRelationCollection relations = new FeatureRelationCollection();
		//add relations
		for(GTFeature tar : target){
			List<GTFeature> intersections = reference.boundsIntersect(tar, dThreshold);
			for(GTFeature ref : intersections){
				IFeatureRelation relation = relation(ref, tar, null, true);
				if(relation != null)
					relations.add(relation);
			}
		}
		return relations;
	}
	
	@Override
	protected IRelationMeasurement[] getMeasurements(IFeature reference, IFeature target){
		//get geometries
		Envelope eReference = ((GTFeature) reference).getDefaultGeometry().getEnvelopeInternal();
		Envelope eTarget = ((GTFeature) target).getDefaultGeometry().getEnvelopeInternal();
		if(eReference.isNull() || eTarget.isNull())
			return null;
		//get overlap
		boolean bIntersect = getIntersect(eReference, eTarget);
		//check for overlap		
		if(bIntersect) {
			return getMeasurements(new RelationMeasurement(
					null, 
					RDFVocabulary.PROPERTY_GEOM.getResource(),
					RDFVocabulary.PROPERTY_GEOM.getResource(),
					new BooleanLiteral(bIntersect), 
					intersectDescription));
		}
		else {
			//get distance
			double dDistance = getDistance(eReference, eTarget);
			//check for overlap
			if(dDistance <= dThreshold)
				return getMeasurements(new RelationMeasurement(
						null, 
						RDFVocabulary.PROPERTY_GEOM.getResource(),
						RDFVocabulary.PROPERTY_GEOM.getResource(),
						new DecimalLiteral(dDistance), 
						distanceDescription));
			//return null if distance > threshold
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
	private boolean getIntersect(Envelope gReference, Envelope gTarget){
		return gReference.intersects(gTarget);
	}
	
	/**
	 * get distance between geometries
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return distance (uom defined by input geometries)
	 */
	private double getDistance(Envelope gReference, Envelope gTarget){
		return gReference.distance(gTarget);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Bounding box distance calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on geometry bounding box";
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
			inputDescriptions.add(new InputDescription(IN_THRESHOLD, IN_THRESHOLD, "Threshold for bounding box distance",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{DecimalLiteral.class})
					},
					new DecimalLiteral(0)));
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
					OUT_RELATIONS, OUT_RELATIONS, "Output relations with bounding box relation",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
		}
		return outputDescriptions;
	}

}
