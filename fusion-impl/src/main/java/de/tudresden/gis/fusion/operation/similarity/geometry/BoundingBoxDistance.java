package de.tudresden.gis.fusion.operation.similarity.geometry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Envelope;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class BoundingBoxDistance extends AbstractMeasurementOperation {

	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#BBoxDistance";
	private final String RELATION_BBOX_OVERLAP = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#overlap_bbox";
	private final String RELATION_BBOX_DISTANCE = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#distance_bbox";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		DecimalLiteral inThreshold = (DecimalLiteral) getInput(IN_THRESHOLD);
		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (GTFeatureRelationCollection) getInput(IN_RELATIONS), inThreshold.getValue()) :
				calculateRelation(inReference, inTarget, inThreshold.getValue()));
			
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
		//get bounding boxes
		ReferencedEnvelope eReference = getEnvelope(reference.getDefaultSpatialProperty());
		ReferencedEnvelope eTarget = getEnvelope(target.getDefaultSpatialProperty());
		//check for overlap
		boolean overlap = intersects(eReference, eTarget);
		if(overlap){
			double dOverlap = getOverlap(eReference, eTarget);
			return new SimilarityMeasurement(
					new DecimalLiteral(dOverlap), 
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_BBOX_OVERLAP)))
			);
		}
		else {
			//get distance
			double distance = getDistance(eReference, eTarget);
			if(distance <= dThreshold)
				return new SimilarityMeasurement(
						new DecimalLiteral(distance), 
						this.getMeasurementDescription(new RelationType(new IRI(RELATION_BBOX_DISTANCE)))
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
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
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
					"Bounding box overlap between geometries", 
					new RelationType(new IRI(RELATION_BBOX_OVERLAP)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(100)},  
							false
					))
		);
		measurements.add(new MeasurementDescription(
					this.getProcessIRI(),
					"Bounding box distance between geometries", 
					new RelationType(new IRI(RELATION_BBOX_DISTANCE)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(Double.MAX_VALUE)}, 
							true
					))
		);
		return measurements;
	}	

}
