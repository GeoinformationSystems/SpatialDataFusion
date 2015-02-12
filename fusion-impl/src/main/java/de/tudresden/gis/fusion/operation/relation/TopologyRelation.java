package de.tudresden.gis.fusion.operation.relation;

import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;

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
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class TopologyRelation extends AbstractMeasurementOperation {

	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#TopologyRelation";

	//DE-9IM topology relation
	private final String TOPOLOGY_DE9IM = "http://tu-dresden.de/uw/geo/gis/fusion/relation/topology#de-9im";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
	
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS)) :
				calculateRelation(inReference, inTarget));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	SimilarityMeasurement similarity = getTopologyRelation(fRef, fTar);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
		
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations){
		
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = getTopologyRelation(fReference, fTarget);
    		if(similarity != null)
    			relation.addRelationMeasurement(similarity);
	    }
		return relations;
	    
	}
	
	/**
	 * check topoogy relation between input features
	 * @param reference reference feature
	 * @param target target feature
	 * @return
	 */
	private SimilarityMeasurement getTopologyRelation(IFeature reference, IFeature target) {
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
					this.getMeasurementDescription(new RelationType(new IRI(TOPOLOGY_DE9IM)))
			);
		}
		else return null;
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
		return "Determines topology relation between input geometries, stored as DE-9IM";
	}
	
	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
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
					"DE-9IM intersection pattern for input geometries", 
					new RelationType(new IRI(TOPOLOGY_DE9IM)),
					new MeasurementRange<String>(
							new StringLiteral[]{new StringLiteral("FFFFFFFFF"), new StringLiteral("TTTTTTTTT")}, 
							true
					))
		);
		return measurements;
	}
	
}
