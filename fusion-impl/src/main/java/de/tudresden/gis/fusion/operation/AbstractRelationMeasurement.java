package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;

public abstract class AbstractRelationMeasurement extends AbstractMeasurementOperation {
	
	protected IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	SimilarityMeasurement similarity = calculateSimilarity(fRef, fTar);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
		
	protected IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations){
		//init tmp relations
		IFeatureRelationCollection tmpRelations = new GTFeatureRelationCollection();
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = calculateSimilarity(fReference, fTarget);
    		if(similarity == null && dropRelations())
    			continue;
			if(similarity != null)
    			relation.addRelationMeasurement(similarity);
			tmpRelations.addRelation(relation);
	    }
		return tmpRelations;
	    
	}
	
	protected abstract SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target);

	private boolean bDropRelations = false;
	protected boolean dropRelations() { return bDropRelations; }
	protected void setDropRelations(BooleanLiteral dropRelations) { 
		if(dropRelations != null)
			this.bDropRelations = dropRelations.getValue(); 
	}
	
}
