package de.tudresden.gis.fusion.operation;

import java.util.Arrays;
import java.util.HashSet;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.ISimilarityMeasurement;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;
import de.tudresden.gis.fusion.metadata.operation.ISimilarityMeasurementOperationProfile;
import de.tudresden.gis.fusion.metadata.operation.SimilarityMeasurementOperationProfile;

public abstract class ASimilarityMeasurementOperation extends ARelationMeasurementOperation implements ISimilarityMeasurementOperation {
	
	@Override
	public ISimilarityMeasurementOperationProfile getProfile(){
		if(profile == null)
			setProfile(new SimilarityMeasurementOperationProfile(
					getResource().getIdentifier(),
					new HashSet<IIdentifiableResource>(Arrays.asList(getClassification())),
					getProcessTitle(),
					getProcessDescription(),
					new HashSet<IIODescription>(Arrays.asList(getInputDescriptions())),
					new HashSet<IIODescription>(Arrays.asList(getOutputDescriptions())),
					new HashSet<ISimilarityMeasurementDescription>(Arrays.asList(getSupportedMeasurements()))
			));
		return (ISimilarityMeasurementOperationProfile) profile;
	}
	
	@Override
	protected abstract ISimilarityMeasurementDescription[] getSupportedMeasurements();
	
	/**
	 * computation of a similarity relation between two input features
	 * @param reference reference feature
	 * @param target target feature
	 * @return similarity measurement for the input features
	 */
	protected abstract ISimilarityMeasurement relate(IFeature reference, IFeature target);
	
	/**
	 * indicated if existing relations shall be dropped, if similarity measurement is null
	 * @return true, if relations shall be dropped 
	 */
	protected abstract boolean dropRelations();
	
	/**
	 * function to relate two feature collections based on the abstract relate method
	 * @param reference reference collection
	 * @param target target collection
	 * @return collection of feature relations
	 */
	protected IFeatureRelationCollection relate(IFeatureCollection reference, IFeatureCollection target) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	ISimilarityMeasurement similarity = relate(fRef, fTar);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
		
	/**
	 * function to relate two feature collections based on the abstract relate method
	 * @param reference reference collection
	 * @param target target collection
	 * @param relations existing relations between inputs
	 * @return collection of feature relations
	 */
	protected IFeatureRelationCollection relate(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations){
		//init tmp relations
		IFeatureRelationCollection tmpRelations = new GTFeatureRelationCollection();
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getFeatureId());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getFeatureId());
			if(reference == null || target == null)
				continue;
			ISimilarityMeasurement similarity = relate(fReference, fTarget);
    		if(similarity == null && dropRelations())
    			continue;
			if(similarity != null)
    			relation.addRelationMeasurement(similarity);
			tmpRelations.addRelation(relation);
	    }
		return tmpRelations;
	    
	}
	
	@Override
	public ISimilarityMeasurementDescription getMeasurementDescription(IIRI identifier){
		for(ISimilarityMeasurementDescription desc : getSupportedMeasurements()){
			if(desc.getRDFRepresentation().getSubject().getIdentifier().equals(identifier))
				return desc;
		}
		return null;
	}
	
}
