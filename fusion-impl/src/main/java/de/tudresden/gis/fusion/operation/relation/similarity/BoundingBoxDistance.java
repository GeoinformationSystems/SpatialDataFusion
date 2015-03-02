package de.tudresden.gis.fusion.operation.relation.similarity;

import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Envelope;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
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

public class BoundingBoxDistance extends ASimilarityMeasurementOperation {

	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";	
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_LOC.resource()
	};
	
	private final IIRI MEASUREMENT_OVERLAP_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#overlap");
	private final String MEASUREMENT_OVERLAP_DESC = "Overlap between bounding boxes";
	private final IIdentifiableResource[] MEASUREMENT_OVERLAP_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.TOPO_OVERLAP.resource(),
			EMeasurementType.TOPO_INTERSECT.resource()
	};
	private final IIRI MEASUREMENT_DIST_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle() + "#distance");
	private final String MEASUREMENT_DIST_DESC = "Distance between bounding boxes";
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
				relate(inReference, inTarget, (GTFeatureRelationCollection) getInput(IN_RELATIONS)) :
				calculateRelationDelegate(inReference, inTarget));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	@Override
	protected boolean dropRelations() {
		return bDropRelations;
	}
	
	private IFeatureRelationCollection calculateRelationDelegate(IFeatureCollection reference, IFeatureCollection target) {

		if(reference instanceof GTIndexedFeatureCollection)
			return calculateRelationWithIndex((GTIndexedFeatureCollection) reference, target);
		else
			return relate(reference, target);
		
	}
	
	private IFeatureRelationCollection calculateRelationWithIndex(GTIndexedFeatureCollection reference, IFeatureCollection target) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fTar : target) {
		    List<IFeature> intersections = reference.intersects(fTar, dThreshold);
		    for(IFeature fRef : intersections){
		    	SimilarityMeasurement similarity = relate(fRef, fTar);
		    	//only adds measurement, if distance is <= threshold (index can return features that are more distant by different expand strategy)
		    	if(similarity != null)
		    		relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
	
	@Override
	protected SimilarityMeasurement relate(IFeature reference, IFeature target) {
		//get bounding boxes
		ReferencedEnvelope eReference = getEnvelope(reference.getDefaultSpatialProperty());
		ReferencedEnvelope eTarget = getEnvelope(target.getDefaultSpatialProperty());
		//check for overlap
		boolean overlap = intersects(eReference, eTarget);
		if(overlap){
			double dOverlap = getOverlap(eReference, eTarget);
			return new SimilarityMeasurement( 
				new DecimalLiteral(dOverlap),
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_OVERLAP_ID)
			);
		}
		else {
			//get distance
			double dDistance = getDistance(eReference, eTarget);
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
	
	private ReferencedEnvelope getEnvelope(ISpatialProperty property){
		double[] bbox = property.getBounds();
		return new ReferencedEnvelope(bbox[0], bbox[2], bbox[1], bbox[3], null);
	}
	
	private double getDistance(ReferencedEnvelope eReference, ReferencedEnvelope eTarget){
		return eReference.distance(eTarget);
	}
	
	private boolean intersects(ReferencedEnvelope eReference, ReferencedEnvelope eTarget){
		return eReference.intersects((BoundingBox) eTarget);
	}
	
	private double getOverlap(ReferencedEnvelope eReference, ReferencedEnvelope eTarget){
		if(eReference.equals(eTarget))
			return 100d;
		Envelope eIntersection = eReference.intersection(eTarget);
		if(eIntersection != null)
			return((getArea(new ReferencedEnvelope(eIntersection, eReference.getCoordinateReferenceSystem())) / getArea(eReference)) * 100);
		else
			return 0d;
	}
	
	private double getArea(ReferencedEnvelope envelope){
		return envelope.getArea();
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
		return "Calculates the distance between input feature bounds";
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
					MEASUREMENT_OVERLAP_ID, MEASUREMENT_OVERLAP_DESC,
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(100)},
							true
					),
					DataUtilities.toSet(MEASUREMENT_OVERLAP_CLASSIFICATION)
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
