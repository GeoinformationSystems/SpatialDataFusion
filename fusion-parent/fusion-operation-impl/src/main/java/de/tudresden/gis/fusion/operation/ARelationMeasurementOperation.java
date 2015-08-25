package de.tudresden.gis.fusion.operation;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.relation.FeatureRelation;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;

public abstract class ARelationMeasurementOperation extends AOperationInstance {
	
	/**
	 * create relation collection for this process
	 * @param reference reference feature views
	 * @param target target feature views
	 * @return feature view relation collection
	 */
	protected IDataCollection<IRelation<IFeatureView>> relations(IDataCollection<? extends IFeatureView> reference, IDataCollection<? extends IFeatureView> target){
		//create relation collection
		IDataCollection<IRelation<IFeatureView>> relations = new FeatureRelationCollection();
		//add relations
		for(IFeatureView ref : reference){
			for(IFeatureView tar : target){
				IRelation<IFeatureView> relation = relation(ref, tar, null, true);
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
	protected IDataCollection<IRelation<IFeatureView>> relations(IDataCollection<? extends IFeatureView> reference, IDataCollection<? extends IFeatureView> target, IDataCollection<IRelation<IFeatureView>> existingRelations, boolean drop){
		//create relation collection
		IDataCollection<IRelation<IFeatureView>> relations = new FeatureRelationCollection();
		//add relation measurement if relation already exists & measurement != null
		for(IRelation<IFeatureView> existingRelation : existingRelations){
			IFeatureView ref = existingRelation.source();
			IFeatureView tar = existingRelation.target();
			IRelation<IFeatureView> relation = relation(ref, tar, existingRelation, drop);
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
	protected IRelation<IFeatureView> relation(IFeatureView reference, IFeatureView target, IRelation<IFeatureView> relation, boolean drop){
		IRelationMeasurement<? extends Comparable<?>> measurement = null;
		try{
			measurement = measurement(reference, target);
		} catch(ProcessException e){
			//continue
		}
		//add measurement if not null
		if(measurement != null){
			if(relation == null)
				relation = new FeatureRelation<IFeatureView>(reference, target);
			relation.add(measurement);
			return relation;
		}
		else {
			return drop ? null : relation;
		}
	}
	
	/**
	 * execute feature view measurement for this process
	 * @param reference reference feature view
	 * @param target target feature view
	 * @return feature view measurement
	 */
	protected abstract IRelationMeasurement<? extends Comparable<?>> measurement(IFeatureView reference, IFeatureView target);
	
}
