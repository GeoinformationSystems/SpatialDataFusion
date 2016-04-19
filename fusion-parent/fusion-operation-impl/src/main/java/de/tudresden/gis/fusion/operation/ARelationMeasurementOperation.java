package de.tudresden.gis.fusion.operation;

import java.util.List;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.relation.FeatureRelation;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;

public abstract class ARelationMeasurementOperation extends AOperationInstance {
	
	/**
	 * create collection of feature relations from input features
	 * @param reference reference features
	 * @param target target features
	 * @return feature relation collection
	 */
	protected IDataCollection<IFeatureRelation> relations(IDataCollection<? extends IFeature> reference, IDataCollection<? extends IFeature> target){
		//create relation collection
		FeatureRelationCollection relations = new FeatureRelationCollection();
		//add relations
		for(IFeature ref : reference){
			for(IFeature tar : target){
				IFeatureRelation relation = relation(ref, tar, null, true);
				if(relation != null)
					relations.add(relation);
			}
		}
		return relations;
	}
	
	/**
	 * create relation collection for this process
	 * @param reference source feature views
	 * @param target target feature views
	 * @param existingRelations existing relations between source and target views
	 * @param drop if true, relations are dropped if measurement returns null
	 * @return feature view relation collection
	 */
	protected IDataCollection<IFeatureRelation> relations(IDataCollection<? extends IFeature> reference, IDataCollection<? extends IFeature> target, IDataCollection<IFeatureRelation> existingRelations, boolean drop){
		//create relation collection
		FeatureRelationCollection relations = new FeatureRelationCollection();
		//add relation measurement if relation already exists & measurement != null
		for(IFeatureRelation existingRelation : existingRelations){
			IFeature ref = existingRelation.getSource();
			IFeature tar = existingRelation.getTarget();
			IFeatureRelation relation = relation(ref, tar, existingRelation, drop);
			if(relation != null)
				relations.add(relation);
		}
		return relations;
	}
	
	/**
	 * create relation for this process
	 * @param reference reference feature view
	 * @param target target feature view
	 * @param relation relation between views (a new relation is created if relation == null)
	 * @param drop if true, this method returns null if measurement returns null
	 * @return feature view relation
	 */
	protected IFeatureRelation relation(IFeature reference, IFeature target, IFeatureRelation relation, boolean drop){
		IRelationMeasurement[] measurements = null;
		try{
			measurements = getMeasurements(reference, target);
		} catch(ProcessException e){
			//TODO: log exception
			//continue
		}
		//add measurements if not null
		if(measurements != null && measurements.length > 0){
			if(relation == null)
				relation = new FeatureRelation(reference, target);
			for(IRelationMeasurement measurement : measurements){
				relation.addMeasurement(measurement);
			}
			return relation;
		}
		else {
			return drop ? null : relation;
		}
	}
	
	/**
	 * execute feature measurements for this process
	 * @param reference reference feature
	 * @param target target feature
	 * @return feature measurements
	 */
	protected abstract IRelationMeasurement[] getMeasurements(IFeature reference, IFeature target);
	
	/**
	 * get array of measurements from single measurement 
	 * @param measurement relation measurement
	 * @return array of measurements
	 */
	protected IRelationMeasurement[] getMeasurements(IRelationMeasurement measurement){
		return new IRelationMeasurement[]{measurement};
	}
	
	/**
	 * get array of measurements from list of measurement 
	 * @param measurement relation measurement
	 * @return array of measurements
	 */
	protected IRelationMeasurement[] getMeasurements(List<IRelationMeasurement> measurements){
		return measurements.toArray(new IRelationMeasurement[measurements.size()]);
	}
	
}
